// "Cast expression 'a + a' to 'B'" "true"
interface A {
    fun plus(x: Any): A
}
interface B : A

fun foo(a: A): B {
    return a + a<caret>
}