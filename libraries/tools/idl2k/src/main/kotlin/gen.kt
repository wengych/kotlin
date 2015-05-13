package org.jetbrains.idl2k

import java.util.*

private fun Operation.getterOrSetter() = this.attributes.map { it.call }.toSet().let { attributes ->
    when {
        "getter" in attributes -> NativeGetterOrSetter.GETTER
        "setter" in attributes -> NativeGetterOrSetter.SETTER
        else -> NativeGetterOrSetter.NONE
    }
}

fun String.ensureNullable() = when {
    endsWith("?") -> this
    this == "dynamic" -> this
    contains("->") -> "($this)?"
    else -> "$this?"
}

fun String.dropNullable() = when {
    endsWith(")?") -> this.removeSuffix("?").removeSurrounding("(", ")")
    endsWith("?") -> this.removeSuffix("?")
    else -> this
}

fun String.copyNullabilityFrom(type : String) = when {
    type.endsWith("?") -> ensureNullable()
    else -> this
}

fun generateFunction(repository: Repository, function: Operation, functionName: String, nativeGetterOrSetter: NativeGetterOrSetter = function.getterOrSetter()): GenerateFunction =
        function.attributes.map { it.call }.toSet().let { attributes ->
            GenerateFunction(
                    name = functionName,
                    returnType = mapType(repository, function.returnType).let { mapped -> if (nativeGetterOrSetter != NativeGetterOrSetter.NONE) mapped.ensureNullable() else mapped },
                    arguments = function.parameters.map {
                        GenerateAttribute(
                                name = it.name,
                                type = mapType(repository, it.type),
                                initializer = it.defaultValue,
                                getterSetterNoImpl = false,
                                override = false,
                                readOnly = true,
                                vararg = it.vararg
                        )
                    },
                    nativeGetterOrSetter = nativeGetterOrSetter
            )
        }

fun generateFunctions(repository: Repository, function: Operation): List<GenerateFunction> {
    val realFunction = if (function.name == "") null else generateFunction(repository, function, function.name, NativeGetterOrSetter.NONE)
    val getterOrSetterFunction = when (function.getterOrSetter()) {
        NativeGetterOrSetter.NONE -> null
        NativeGetterOrSetter.GETTER -> generateFunction(repository, function, "get")
        NativeGetterOrSetter.SETTER -> generateFunction(repository, function, "set")
    }
    val callbackArgumentsAsLambdas = function.parameters.map {
        val interfaceType = repository.interfaces[it.type.dropNullable()]
        when {
            interfaceType == null -> it
            interfaceType.callback -> interfaceType.operations.single().let { callbackFunction ->
                it.copy(type = callbackFunction.parameters
                        .map { mapType(repository, it.type) }
                        .join(",", "(", ") -> ${mapType(repository, callbackFunction.returnType)}")
                        .copyNullabilityFrom(it.type))
            }
            else -> it
        }
    }

    val functionWithCallbackOrNull = when {
        callbackArgumentsAsLambdas == function.parameters -> null
        else -> generateFunction(repository, function.copy(parameters = callbackArgumentsAsLambdas), function.name, NativeGetterOrSetter.NONE)
    }

    return listOf(realFunction, getterOrSetterFunction, functionWithCallbackOrNull).filterNotNull()
}

fun generateAttribute(putNoImpl: Boolean, repository: Repository, attribute: Attribute): GenerateAttribute =
        GenerateAttribute(attribute.name,
                type = mapType(repository, attribute.type),
                initializer = attribute.defaultValue,
                getterSetterNoImpl = putNoImpl,
                readOnly = attribute.readOnly,
                override = false,
                vararg = attribute.vararg
        )

private fun InterfaceDefinition.superTypes(repository: Repository) = superTypes.map { repository.interfaces[it] }.filterNotNull()
private fun resolveDefinitionType(repository: Repository, iface: InterfaceDefinition, constructor: ExtendedAttribute? = iface.findConstructor()): GenerateDefinitionKind =
        if (iface.dictionary || constructor != null || iface.superTypes(repository).any { resolveDefinitionType(repository, it) == GenerateDefinitionKind.CLASS }) {
            GenerateDefinitionKind.CLASS
        }
        else {
            GenerateDefinitionKind.TRAIT
        }

private fun InterfaceDefinition.mapAttributes(repository: Repository) = attributes.map { generateAttribute(!dictionary, repository, it) }
private fun InterfaceDefinition.mapOperations(repository: Repository) = operations.flatMap { generateFunctions(repository, it) }
private fun Constant.mapConstant(repository : Repository) = GenerateAttribute(name, mapType(repository, type), value, false, true, false, false)

fun generateTrait(repository: Repository, iface: InterfaceDefinition): GenerateTraitOrClass {
    val constructor = iface.findConstructor()
    val constructorFunction = generateFunction(repository, Operation("", "Unit", constructor?.arguments ?: emptyList(), emptyList()), functionName = "", nativeGetterOrSetter = NativeGetterOrSetter.NONE)
    val constructorArgumentNames = constructorFunction.arguments.map { it.name }.toSet()

    val constructorSuperCalls = iface.superTypes
            .map { repository.interfaces[it] }
            .filterNotNull()
            .filter { resolveDefinitionType(repository, it) == GenerateDefinitionKind.CLASS }
            .map {
                val superConstructor = it.findConstructor()
                GenerateFunctionCall(
                        name = it.name,
                        arguments = if (superConstructor == null) {
                            emptyList()
                        } else {
                            superConstructor.arguments.map { arg ->
                                if (arg.name in constructorArgumentNames) arg.name else "noImpl"
                            }
                        }
                )
            }

    val entityType = resolveDefinitionType(repository, iface, constructor)
    val extensions = repository.externals[iface.name]?.map { repository.interfaces[it] }?.filterNotNull() ?: emptyList()

    return GenerateTraitOrClass(iface.name, iface.namespace, entityType, iface.superTypes,
            memberAttributes = (iface.mapAttributes(repository) + extensions.flatMap { it.mapAttributes(repository) }).distinct().toList(),
            memberFunctions = (iface.mapOperations(repository) + extensions.flatMap { it.mapOperations(repository) }).distinct().toList(),
            constants = (iface.constants.map {it.mapConstant(repository)} + extensions.flatMap { it.constants.map {it.mapConstant(repository)} }.distinct().toList()),
            constructor = constructorFunction,
            superConstructorCalls = constructorSuperCalls
    )
}

fun mapUnionType(it : UnionType) = GenerateTraitOrClass(
        name = it.name,
        namespace = it.namespace,
        kind = GenerateDefinitionKind.TRAIT,
        superTypes = emptyList(),
        memberAttributes = emptyList(),
        memberFunctions = emptyList(),
        constants = emptyList(),
        constructor = null,
        superConstructorCalls = emptyList()
)

fun generateUnionTypeTraits(allUnionTypes : Iterable<UnionType>): List<GenerateTraitOrClass> = allUnionTypes.map(::mapUnionType)

fun mapDefinitions(repository: Repository, definitions: Iterable<InterfaceDefinition>) =
        definitions.filter { "NoInterfaceObject" !in it.extendedAttributes.map { it.call } }.map { generateTrait(repository, it) }
