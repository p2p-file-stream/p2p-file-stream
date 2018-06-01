package com.github.p2pfilestream.encoding

import com.google.testing.compile.CompilationRule
import com.squareup.kotlinpoet.*
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter.methodsIn
import kotlin.reflect.KClass

private val compilation = CompilationRule()

private fun getElement(clazz: KClass<*>): TypeElement {
    return compilation.elements.getTypeElement(clazz.java.canonicalName)
}


fun main(args: Array<String>) {
    val interfaceClass = Class.forName(args[0]).kotlin
    val packageAndInterface = args[0].split(".")
    val packageName = packageAndInterface
        .dropLast(1).joinToString(".")
    val interfaceName = packageAndInterface.last()
    val className = "${interfaceName}Encoder"
    val lambdaTypeName = LambdaTypeName.get(parameters = *arrayOf(ByteArray::class.asClassName()), returnType = UNIT)
    val classElement = getElement(interfaceClass)
    val functions = methodsIn(classElement.enclosedElements).map {
        FunSpec.overriding(it).build()
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
                    PropertySpec.builder("receiver", lambdaTypeName)
                        .initializer("receiver")
                        .build()
                )
                .addFunctions(functions)
                .build()
        )
        .build()

    file.writeTo(System.out)
}