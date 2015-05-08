// "Change to short enum constructor" "true"

enum class MyEnum(val z: Int) {
    A(3)
    B(7)
    C<caret>: MyEnum(12)
    fun foo() = z * 2
}