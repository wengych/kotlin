// "Change 'A.not' function return type to 'A'" "true"
interface A {
    fun not(): String
    fun times(a: A): A
}

fun foo(a: A): A = a * <caret>!(if (true) a else a)