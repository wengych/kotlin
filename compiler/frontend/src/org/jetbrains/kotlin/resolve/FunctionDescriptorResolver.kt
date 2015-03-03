/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.resolve

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.impl.ConstructorDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.FunctionExpressionDescriptor
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
import org.jetbrains.kotlin.diagnostics.Errors.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.scopes.JetScope
import org.jetbrains.kotlin.resolve.ModifiersChecker.*
import org.jetbrains.kotlin.resolve.DescriptorUtils.*
import org.jetbrains.kotlin.resolve.DescriptorResolver.*
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.scopes.WritableScope
import org.jetbrains.kotlin.resolve.scopes.WritableScopeImpl
import org.jetbrains.kotlin.resolve.source.toSourceElement
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.DeferredType
import org.jetbrains.kotlin.types.ErrorUtils
import org.jetbrains.kotlin.types.JetType
import org.jetbrains.kotlin.types.expressions.ExpressionTypingServices
import java.util.ArrayList

class FunctionDescriptorResolver(
        val typeResolver: TypeResolver,
        val descriptorResolver: DescriptorResolver,
        val annotationResolver: AnnotationResolver,
        val storageManager: StorageManager,
        val expressionTypingServices: ExpressionTypingServices,
        val builtIns: KotlinBuiltIns
) {
    public fun resolveFunctionDescriptor(
            containingDescriptor: DeclarationDescriptor,
            scope: JetScope,
            function: JetNamedFunction,
            trace: BindingTrace,
            dataFlowInfo: DataFlowInfo
    ): SimpleFunctionDescriptor {
        if (function.getName() == null) {
            trace.report(FUNCTION_DECLARATION_WITH_NO_NAME.on(function))
        }
        val functionDescriptor = SimpleFunctionDescriptorImpl.create(
                containingDescriptor,
                annotationResolver.resolveAnnotationsWithArguments(scope, function.getModifierList(), trace),
                function.getNameAsSafeName(),
                CallableMemberDescriptor.Kind.DECLARATION,
                function.toSourceElement()
        )
        initializeFunctionDescriptorAndExplicitReturnType(containingDescriptor, scope, function, functionDescriptor, trace)
        initializeFunctionReturnTypeBasedOnFunctionBody(scope, function, functionDescriptor, trace, dataFlowInfo)
        BindingContextUtils.recordFunctionDeclarationToDescriptor(trace, function, functionDescriptor)
        return functionDescriptor
    }

    public fun resolveFunctionExpressionDescriptor(
            containingDescriptor: DeclarationDescriptor,
            scope: JetScope,
            function: JetNamedFunction,
            trace: BindingTrace,
            dataFlowInfo: DataFlowInfo
    ): SimpleFunctionDescriptor {
        val functionDescriptor = FunctionExpressionDescriptor(
                containingDescriptor,
                annotationResolver.resolveAnnotationsWithArguments(scope, function.getModifierList(), trace),
                function.getNameAsSafeName(),
                CallableMemberDescriptor.Kind.DECLARATION,
                function.toSourceElement()
        )
        initializeFunctionDescriptorAndExplicitReturnType(containingDescriptor, scope, function, functionDescriptor, trace)
        initializeFunctionReturnTypeBasedOnFunctionBody(scope, function, functionDescriptor, trace, dataFlowInfo)
        BindingContextUtils.recordFunctionDeclarationToDescriptor(trace, function, functionDescriptor)
        return functionDescriptor
    }

    private fun initializeFunctionReturnTypeBasedOnFunctionBody(
            scope: JetScope,
            function: JetNamedFunction,
            functionDescriptor: SimpleFunctionDescriptorImpl,
            trace: BindingTrace,
            dataFlowInfo: DataFlowInfo
    ) {
        if (functionDescriptor.getReturnType() != null) return
        assert(function.getTypeReference() == null) {
            "Return type must be initialized early for function: " + function.getText() + ", at: " + DiagnosticUtils.atLocation(function) }

        val returnType = if (function.hasBlockBody()) {
            builtIns.getUnitType()
        }
        else if (function.hasBody()) {
            DeferredType.createRecursionIntolerant(storageManager, trace) {
                val type = expressionTypingServices.getBodyExpressionType(trace, scope, dataFlowInfo, function, functionDescriptor)
                transformAnonymousTypeIfNeeded(functionDescriptor, function, type, trace)
            }
        }
        else {
            ErrorUtils.createErrorType("No type, no body")
        }
        functionDescriptor.setReturnType(returnType)
    }

    private fun initializeFunctionDescriptorAndExplicitReturnType(
            containingDescriptor: DeclarationDescriptor,
            scope: JetScope,
            function: JetFunction,
            functionDescriptor: SimpleFunctionDescriptorImpl,
            trace: BindingTrace
    ) {
        val innerScope = WritableScopeImpl(scope, functionDescriptor, TraceBasedRedeclarationHandler(trace), "Function descriptor header scope")
        innerScope.addLabeledDeclaration(functionDescriptor)

        val typeParameterDescriptors = descriptorResolver.
                resolveTypeParametersForCallableDescriptor(functionDescriptor, innerScope, function.getTypeParameters(), trace)
        innerScope.changeLockLevel(WritableScope.LockLevel.BOTH)
        descriptorResolver.resolveGenericBounds(function, functionDescriptor, innerScope, typeParameterDescriptors, trace)

        val receiverTypeRef = function.getReceiverTypeReference()
        val receiverType =  if (receiverTypeRef != null) {
            typeResolver.resolveType(innerScope, receiverTypeRef, trace, true)
        } else null

        val valueParameterDescriptors = resolveValueParameters(functionDescriptor, innerScope, function.getValueParameters(), trace)

        innerScope.changeLockLevel(WritableScope.LockLevel.READING)

        val returnTypeRef = function.getTypeReference()
        val returnType = if (returnTypeRef != null) {
            typeResolver.resolveType(innerScope, returnTypeRef, trace, true)
        } else null

        val modality = resolveModalityFromModifiers(function, getDefaultModality(containingDescriptor, function.hasBody()))
        val visibility = resolveVisibilityFromModifiers(function, getDefaultVisibility(function, containingDescriptor))
        functionDescriptor.initialize(
                receiverType,
                getDispatchReceiverParameterIfNeeded(containingDescriptor),
                typeParameterDescriptors,
                valueParameterDescriptors,
                returnType,
                modality,
                visibility
        )
    }

    public fun resolvePrimaryConstructorDescriptor(
            scope: JetScope,
            classDescriptor: ClassDescriptor,
            classElement: JetClass,
            trace: BindingTrace
    ): ConstructorDescriptorImpl? {
        if (classDescriptor.getKind() == ClassKind.ENUM_ENTRY || !classElement.hasPrimaryConstructor()) return null
        return createConstructorDescriptor(
                scope,
                classDescriptor,
                true,
                classElement.getPrimaryConstructorModifierList(),
                classElement,
                classDescriptor.getTypeConstructor().getParameters(),
                classElement.getPrimaryConstructorParameters(),
                trace
        )
    }

    public fun resolveSecondaryConstructorDescriptor(
            scope: JetScope,
            classDescriptor: ClassDescriptor,
            constructor: JetSecondaryConstructor,
            trace: BindingTrace
    ): ConstructorDescriptorImpl {
        return createConstructorDescriptor(
                scope,
                classDescriptor,
                false,
                constructor.getModifierList(),
                constructor,
                classDescriptor.getTypeConstructor().getParameters(),
                constructor.getValueParameters(),
                trace
        )
    }

    private fun createConstructorDescriptor(
            scope: JetScope,
            classDescriptor: ClassDescriptor,
            isPrimary: Boolean,
            modifierList: JetModifierList?,
            declarationToTrace: JetDeclaration,
            typeParameters: List<TypeParameterDescriptor>,
            valueParameters: List<JetParameter>,
            trace: BindingTrace
    ): ConstructorDescriptorImpl {
        val constructorDescriptor = ConstructorDescriptorImpl.create(
                classDescriptor,
                annotationResolver.resolveAnnotationsWithoutArguments(scope, modifierList, trace),
                isPrimary,
                declarationToTrace.toSourceElement()
        )
        trace.record(BindingContext.CONSTRUCTOR, declarationToTrace, constructorDescriptor)
        val parameterScope = WritableScopeImpl(
                scope,
                constructorDescriptor,
                TraceBasedRedeclarationHandler(trace),
                "Scope with value parameters of a constructor"
        )
        parameterScope.changeLockLevel(WritableScope.LockLevel.BOTH)
        val constructor = constructorDescriptor.initialize(
                typeParameters,
                resolveValueParameters(constructorDescriptor, parameterScope, valueParameters, trace),
                resolveVisibilityFromModifiers(
                        modifierList,
                        DescriptorUtils.getDefaultConstructorVisibility(classDescriptor)
                )
        )
        if (DescriptorUtils.isAnnotationClass(classDescriptor)) {
            CompileTimeConstantUtils.checkConstructorParametersType(valueParameters, trace)
        }
        return constructor
    }

    private fun resolveValueParameters(
            functionDescriptor: FunctionDescriptor,
            parameterScope: WritableScope,
            valueParameters: List<JetParameter>,
            trace: BindingTrace
    ): List<ValueParameterDescriptor> {
        val result = ArrayList<ValueParameterDescriptor>()
        for (i in valueParameters.indices) {
            val valueParameter = valueParameters.get(i)
            val typeReference = valueParameter.getTypeReference()

            val type: JetType
            if (typeReference == null) {
                trace.report(VALUE_PARAMETER_WITH_NO_TYPE_ANNOTATION.on(valueParameter))
                type = ErrorUtils.createErrorType("Type annotation was missing")
            }
            else {
                type = typeResolver.resolveType(parameterScope, typeReference, trace, true)
            }

            if (functionDescriptor !is ConstructorDescriptor) {
                DescriptorResolver.checkParameterHasNoValOrVar(trace, valueParameter, VAL_OR_VAR_ON_FUN_PARAMETER)
                DescriptorResolver.checkParameterHasNoModifier(trace, valueParameter)
            }
            else {
                checkConstructorParameterHasNoModifier(trace, valueParameter)
            }

            val valueParameterDescriptor = descriptorResolver.resolveValueParameterDescriptor(parameterScope, functionDescriptor,
                                                                                              valueParameter, i, type, trace)
            parameterScope.addVariableDescriptor(valueParameterDescriptor)
            result.add(valueParameterDescriptor)
        }
        return result
    }

    private fun checkConstructorParameterHasNoModifier(trace: BindingTrace, parameter: JetParameter) {
        // If is not a property, then it must have no modifier
        if (!parameter.hasValOrVarNode()) {
            DescriptorResolver.checkParameterHasNoModifier(trace, parameter)
        }
    }
}