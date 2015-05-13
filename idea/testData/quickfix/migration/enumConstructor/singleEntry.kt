// "Change to short enum constructor" "true"

enum class SimpleEnum(val z: String) {
    UNIQUE: SimpleEnum("42")<caret>
}