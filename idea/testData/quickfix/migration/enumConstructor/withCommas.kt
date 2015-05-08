// "Change to short enum constructor" "true"

enum class SimpleEnum(val z: String = "xxx") {
    FIRST(),
    SECOND<caret>: SimpleEnum("42"),
    LAST("13");

    fun foo() = z
}