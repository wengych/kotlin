// "Change to short enum constructor" "true"

enum class SimpleEnum(val z: String) {
    UNIQUE<caret>: SimpleEnum("42")
}