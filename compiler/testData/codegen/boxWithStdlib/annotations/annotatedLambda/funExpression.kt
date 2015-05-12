import java.lang.annotation.*
import java.lang.reflect.Method
import kotlin.reflect.jvm.java
import kotlin.test.assertEquals

Retention(RetentionPolicy.RUNTIME)
annotation class Ann(val x: String)

fun foo0(block: () -> Unit) = block.javaClass
fun foo1(block: (String) -> Unit) = block.javaClass

fun testMethod(method: Method, name: String) {
    assertEquals("OK", method.getAnnotation(javaClass<Ann>()).x, "On method of test named `$name`")

    for ((index, annotations) in method.getParameterAnnotations().withIndex()) {
        val ann = annotations.filterIsInstance<Ann>().single()
        assertEquals("OK$index", ann.x, "On parameter $index of test named `$name`")
    }
}

fun testClass(clazz: Class<*>, name: String) {
    val invokes = clazz.getMethods().filter { it.getName() == "invoke" }
    assertEquals(2, invokes.size())

    testMethod(invokes[0], name + "$0")
    testMethod(invokes[1], name + "$1")
}

fun box(): String {
    testClass(foo0( @Ann("OK") fun() {} ), "1")
    testClass(foo1( @Ann("OK") fun(@Ann("OK0") x: String) {} ), "2")
    return "OK"
}
