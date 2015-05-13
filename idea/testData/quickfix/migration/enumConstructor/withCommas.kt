// "Change to short enum constructor" "true"

enum class SimpleEnum(val z: String = "xxx") {
    FIRST(),
    SECOND: SimpleEnum("42")<caret>,
    LAST("13");

    fun foo() = z
}