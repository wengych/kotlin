/*
 * Copyright 2010-2014 JetBrains s.r.o.
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

package org.jetbrains.jet.plugin.decompiler.stubBuilder

import com.intellij.psi.stubs.StubElement
import org.jetbrains.jet.descriptors.serialization.Flags
import org.jetbrains.jet.descriptors.serialization.ProtoBuf
import org.jetbrains.jet.lang.psi.stubs.elements.JetClassElementType
import org.jetbrains.jet.lang.psi.stubs.impl.KotlinClassStubImpl
import org.jetbrains.jet.lang.psi.stubs.impl.KotlinObjectStubImpl
import com.intellij.psi.PsiElement
import org.jetbrains.jet.lang.psi.JetClassBody
import org.jetbrains.jet.lang.psi.stubs.impl.KotlinPlaceHolderStubImpl
import org.jetbrains.jet.lang.psi.stubs.elements.JetStubElementTypes
import org.jetbrains.jet.lang.psi.JetParameterList
import org.jetbrains.jet.lang.resolve.name.ClassId
import org.jetbrains.jet.lang.psi.JetDelegationSpecifierList
import org.jetbrains.jet.lang.psi.JetDelegatorToSuperClass
import org.jetbrains.jet.lexer.JetTokens
import org.jetbrains.jet.lang.resolve.name.SpecialNames.getClassObjectName
import org.jetbrains.jet.lang.psi.JetClassObject
import org.jetbrains.jet.descriptors.serialization.descriptors.ProtoContainer
import org.jetbrains.jet.lang.psi.stubs.impl.KotlinModifierListStubImpl
import org.jetbrains.jet.lexer.JetModifierKeywordToken
import org.jetbrains.jet.descriptors.serialization.ProtoBuf.Type
import org.jetbrains.jet.plugin.decompiler.stubBuilder.FlagsToModifiers.*
import org.jetbrains.jet.lang.types.lang.KotlinBuiltIns


fun createClassStub(parent: StubElement<out PsiElement>, classProto: ProtoBuf.Class, classId: ClassId, context: ClsStubBuilderContext) {
    ClassClsStubBuilder(parent, classProto, classId, context).build()
}

private class ClassClsStubBuilder(
        private val parentStub: StubElement<out PsiElement>,
        private val classProto: ProtoBuf.Class,
        private val classId: ClassId,
        private val outerContext: ClsStubBuilderContext
) {
    private val c = outerContext.child(classProto.getTypeParameterList(), classId.getRelativeClassName().shortName())
    private val typeStubBuilder = TypeClsStubBuilder(c)
    private val classKind = Flags.CLASS_KIND[classProto.getFlags()]
    private val supertypeIds = classProto.getSupertypeList().map {
        type ->
        assert(type.getConstructor().getKind() == Type.Constructor.Kind.CLASS)
        c.nameResolver.getClassId(type.getConstructor().getId())
    }.let {
        supertypeIds ->
        //empty supertype list if single supertype is Any
        if (supertypeIds.singleOrNull()?.let { KotlinBuiltIns.isAny(it.asSingleFqName()) } ?: false) {
            listOf()
        }
        else {
            supertypeIds
        }
    }

    private val classOrObjectStub = createClassOrObjectStubAndModifierListStub()

    fun build() {
        val typeConstraintListData = typeStubBuilder.createTypeParameterListStub(classOrObjectStub, classProto.getTypeParameterList())
        createConstructorStub()
        createDelegationSpecifierList()
        typeStubBuilder.createTypeConstraintListStub(classOrObjectStub, typeConstraintListData)
        createClassBodyAndMemberStubs()
    }

    private fun createClassOrObjectStubAndModifierListStub(): StubElement<out PsiElement> {
        val isClassObject = classKind == ProtoBuf.Class.Kind.CLASS_OBJECT
        if (isClassObject) {
            val classObjectStub = KotlinPlaceHolderStubImpl<JetClassObject>(parentStub, JetStubElementTypes.CLASS_OBJECT)
            val modifierList = createModifierListForClass(classObjectStub)
            val objectDeclarationStub = doCreateClassOrObjectStub(classObjectStub)
            createAnnotationStubs(c.components.annotationLoader.loadClassAnnotations(classProto, c.nameResolver), modifierList)
            return objectDeclarationStub
        }
        else {
            val classOrObjectStub = doCreateClassOrObjectStub(parentStub)
            val modifierList = createModifierListForClass(classOrObjectStub)
            createAnnotationStubs(c.components.annotationLoader.loadClassAnnotations(classProto, c.nameResolver), modifierList)
            return classOrObjectStub
        }
    }

    private fun createModifierListForClass(parent: StubElement<out PsiElement>): KotlinModifierListStubImpl {
        val relevantFlags = arrayListOf(VISIBILITY)
        if (isClass()) {
            relevantFlags.add(INNER)
            relevantFlags.add(MODALITY)
        }
        val additionalModifiers = when (classKind) {
            ProtoBuf.Class.Kind.ENUM_CLASS -> listOf(JetTokens.ENUM_KEYWORD)
            ProtoBuf.Class.Kind.ANNOTATION_CLASS -> listOf(JetTokens.ANNOTATION_KEYWORD)
            else -> listOf<JetModifierKeywordToken>()
        }
        return createModifierListStubForDeclaration(parent, classProto.getFlags(), relevantFlags, additionalModifiers)
    }

    private fun doCreateClassOrObjectStub(parent: StubElement<out PsiElement>): StubElement<out PsiElement> {
        val isClassObject = classKind == ProtoBuf.Class.Kind.CLASS_OBJECT
        val fqName = if (!isClassObject) outerContext.memberFqNameProvider.getMemberFqName(classId.getRelativeClassName().shortName()) else null
        val shortName = fqName?.shortName()?.ref()
        val superTypeRefs = supertypeIds.filter {
            //TODO: filtering function types should go away
            !KotlinBuiltIns.isExactFunctionType(it.asSingleFqName()) && !KotlinBuiltIns.isExactExtensionFunctionType(it.asSingleFqName())
        }.map { it.getRelativeClassName().shortName().ref() }.copyToArray()
        return when (classKind) {
            ProtoBuf.Class.Kind.OBJECT, ProtoBuf.Class.Kind.CLASS_OBJECT -> {
                KotlinObjectStubImpl(
                        parent, shortName, fqName, superTypeRefs,
                        isTopLevel = classId.isTopLevelClass(),
                        isClassObject = isClassObject,
                        isLocal = false,
                        isObjectLiteral = false
                )
            }
            else -> {
                KotlinClassStubImpl(
                        JetClassElementType.getStubType(classKind == ProtoBuf.Class.Kind.ENUM_ENTRY),
                        parent,
                        fqName?.ref(),
                        shortName,
                        superTypeRefs,
                        isTrait = classKind == ProtoBuf.Class.Kind.TRAIT,
                        isEnumEntry = classKind == ProtoBuf.Class.Kind.ENUM_ENTRY,
                        isLocal = false,
                        isTopLevel = classId.isTopLevelClass()
                )
            }
        }
    }

    private fun createConstructorStub() {
        if (!isClass()) return

        val primaryConstructorProto = classProto.getPrimaryConstructor()
        if (primaryConstructorProto.hasData()) {
            typeStubBuilder.createValueParameterListStub(classOrObjectStub, primaryConstructorProto.getData(), ProtoContainer(classProto, null))
        }
        else {
            //default empty constructor
            KotlinPlaceHolderStubImpl<JetParameterList>(classOrObjectStub, JetStubElementTypes.VALUE_PARAMETER_LIST)
        }
    }

    private fun createDelegationSpecifierList() {
        // if single supertype is any then no delegation specifier list is needed
        if (supertypeIds.isEmpty()) return

        val delegationSpecifierListStub =
                KotlinPlaceHolderStubImpl<JetDelegationSpecifierList>(classOrObjectStub, JetStubElementTypes.DELEGATION_SPECIFIER_LIST)

        classProto.getSupertypeList().forEach { type ->
            val superClassStub = KotlinPlaceHolderStubImpl<JetDelegatorToSuperClass>(
                    delegationSpecifierListStub, JetStubElementTypes.DELEGATOR_SUPER_CLASS
            )
            typeStubBuilder.createTypeReferenceStub(superClassStub, type)
        }
    }

    private fun createClassBodyAndMemberStubs() {
        val classBody = KotlinPlaceHolderStubImpl<JetClassBody>(classOrObjectStub, JetStubElementTypes.CLASS_BODY)
        createClassObjectStub(classBody)
        createEnumEntryStubs(classBody)
        createCallableMemberStubs(classBody)
        createInnerAndNestedClasses(classBody)
    }

    private fun createClassObjectStub(classBody: KotlinPlaceHolderStubImpl<JetClassBody>) {
        if (!classProto.hasClassObject() || classKind == ProtoBuf.Class.Kind.OBJECT) {
            return
        }

        val classObjectId = classId.createNestedClassId(getClassObjectName(classId.getRelativeClassName().shortName()))
        createNestedClassStub(classBody, classObjectId)
    }

    private fun createEnumEntryStubs(classBody: KotlinPlaceHolderStubImpl<JetClassBody>) {
        classProto.getEnumEntryList().forEach { id ->
            val name = c.nameResolver.getName(id)
            KotlinClassStubImpl(
                    JetStubElementTypes.ENUM_ENTRY,
                    classBody,
                    qualifiedName = c.memberFqNameProvider.getMemberFqName(name).ref(),
                    name = name.ref(),
                    superNames = array(),
                    isTrait = false,
                    isEnumEntry = true,
                    isLocal = false,
                    isTopLevel = false
            )
        }
    }

    private fun createCallableMemberStubs(classBody: KotlinPlaceHolderStubImpl<JetClassBody>) {
        val container = ProtoContainer(classProto, null)
        for (callableProto in sortCallableStubs(classProto.getMemberList())) {
            createCallableStub(classBody, callableProto, c, container)
        }
    }

    private fun isClass(): Boolean {
        return classKind == ProtoBuf.Class.Kind.CLASS ||
               classKind == ProtoBuf.Class.Kind.ENUM_CLASS ||
               classKind == ProtoBuf.Class.Kind.ANNOTATION_CLASS
    }

    private fun createInnerAndNestedClasses(classBody: KotlinPlaceHolderStubImpl<JetClassBody>) {
        classProto.getNestedClassNameList().forEach { id ->
            val nestedClassId = classId.createNestedClassId(c.nameResolver.getName(id))
            createNestedClassStub(classBody, nestedClassId)
        }
    }

    private fun createNestedClassStub(classBody: StubElement<out PsiElement>, nestedClassId: ClassId) {
        val classData = c.components.classDataFinder.findClassData(nestedClassId)!!
        createClassStub(classBody, classData.getClassProto(), nestedClassId, c.child(classData.getNameResolver()))
    }
}