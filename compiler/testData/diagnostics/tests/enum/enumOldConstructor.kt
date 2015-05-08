enum class MyEnum(val myArg: Int) {
    FIRST(1),
    <!ENUM_USES_DEPRECATED_CONSTRUCTORS!>SECOND<!>: MyEnum(2),
    THIRD(3),
    FOURTH(4)
}