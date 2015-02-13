open class A {
    fun test() = if (this is B) foo(42) else 0
}

class B : A() {
    fun foo(i: Int) = i
}


fun box(): String {
    if (B().test() != 42) return "fail1"
    if (A().test() != 0) return "fail2"
    return "OK"
}
