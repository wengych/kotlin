fun Int?.inc(): Int? { return this }

fun init(): Int? { return 10 }

public fun box() : Int? {
    var i : Int? = init()
    var j = ++i
    j<!UNSAFE_CALL!>.<!>toInt()
    return ++j
}
