// "Change to short enum constructor" "true"

enum class MyEnum(val z: Int) {
    A<caret>: MyEnum(3)
    B(7)
    C(12)
    fun foo() = z * 2
}