// KT-5696 Prohibit type parameters for enum classes

package bug

public enum class Foo<!TYPE_PARAMETERS_IN_ENUM!><T><!> {
    <!ENUM_USES_DEPRECATED_CONSTRUCTORS!>A<!> : Foo<String>()
}
