// "Insert lacking comma(s) / semicolon(s)" "true"

enum class MyEnum<caret> {
    A {
        override fun foo(): Int = 13
    }
    B C {
        override fun foo(): Int = 23
    }
    open fun foo(): Int = 42
}