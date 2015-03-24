fun testProperLineNumberAfterInline(): String {
    var exceptionCount = 0;
    try {
        checkEquals(test(),
                    "12")
    } catch(e: AssertionError) {
        val entry = e.getStackTrace()!![1]
        val actual = "${entry.getFileName()}:${entry.getLineNumber()}"
        if ("stackTrace.kt:4" != actual) {
            return "fail 1: ${actual}"
        }
        exceptionCount++
    }

    try {
        checkEquals("12",
                    test())
    } catch(e: AssertionError) {
        val entry = e.getStackTrace()!![1]
        val actual = "${entry.getFileName()}:${entry.getLineNumber()}"
        if ("stackTrace.kt:16" != actual) {
            return "fail 1: ${actual}"
        }
        exceptionCount++
    }

    return if (exceptionCount == 2) "OK" else "fail"
}

fun testProperLineForOtherParameters(): String {
    var exceptionCount = 0;
    try {
        checkEquals(test(),
                    fail())
    } catch(e: AssertionError) {
        val entry = e.getStackTrace()!![1]
        val actual = "${entry.getFileName()}:${entry.getLineNumber()}"
        e.printStackTrace()
        if ("stackTrace.kt:33" != actual) {
            return "fail 3: ${actual}"
        }
        exceptionCount++
    }

    try {
        checkEquals(fail(),
                    test())
    } catch(e: AssertionError) {
        val entry = e.getStackTrace()!![1]
        val actual = "${entry.getFileName()}:${entry.getLineNumber()}"
        if ("stackTrace.kt:46" != actual) {
            return "fail 4: ${actual}"
        }
        exceptionCount++
    }

    return if (exceptionCount == 2) "O1K" else "fail"
}


fun box(): String {
    val res = testProperLineNumberAfterInline()
    if (res != "OK") return "fail $res"

    return testProperLineForOtherParameters()
}

public fun checkEquals(p1: String, p2: String) {
    throw AssertionError("fail")
}

inline fun test() : String {
    return "123"
}

fun fail() : String {
    throw AssertionError("fail")
}