package com.github.p2pfilestream.encoding

import com.squareup.kotlinpoet.*
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

fun main(args: Array<String>) {
    val interfaceClass = Class.forName(args[0]).kotlin
    val packageAndInterface = args[0].split(".")
    val packageName = packageAndInterface
        .dropLast(1).joinToString(".")
    val interfaceName = packageAndInterface.last()
    val className = "${interfaceName}Encoder"
    val lambdaTypeName = LambdaTypeName.get(parameters = *arrayOf(ByteArray::class.asClassName()), returnType = UNIT)
    val functions = interfaceClass.declaredMemberFunctions.map { method ->
        FunSpec.builder(method.name)
            .addModifiers(KModifier.OVERRIDE)
            .addParameters(method.valueParameters.map {
                ParameterSpec.builder(it.name!!, it.type.jvmErasure).build()
            })
            .addCode("message(::${method.name}, %L)", method.valueParameters.joinToString { it.name!! })
            .build()
    }
    val file = FileSpec.builder(packageName, className)
        .addType(
            TypeSpec.classBuilder(className)
                .addSuperinterface(interfaceClass)
                .superclass(MessageEncoder::class)
                .addSuperclassConstructorParameter("receiver")
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addParameter("receiver", lambdaTypeName)
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("receiver", lambdaTypeName, KModifier.PRIVATE)
                        .initializer("receiver")
                        .build()
                )
                .addFunctions(functions)
                .build()
        )
        .build()

    file.writeTo(System.out)
}