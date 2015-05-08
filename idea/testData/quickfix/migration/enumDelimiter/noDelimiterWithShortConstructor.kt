// "Insert lacking comma(s) / semicolon(s)" "true"

enum class MyEnum<caret>(val z: Int) {
    A(3)  B(7)  C(12)
    fun foo() = z * 2
}