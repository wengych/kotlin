import test.*

fun box(): String {
    var exceptionCount = 0;
    try {
        checkEquals(test(), "12")
    } catch(e: AssertionError) {
        val entry = e.getStackTrace()!![1]
        val actual = "${entry.getFileName()}:${entry.getLineNumber()}"
        if ("exception.1.kt:5" != actual) {
            return "fail ${actual}"
        }
        e.getMessage()!!
    }


    return if (exceptionCount == 2) "OK" else "fail"
}

public fun checkEquals(p1: String, p2: String) {
    if (p1 != p2) {
        throw AssertionError("fail")
    }
}


inline fun test() : String {
    return "123"
}