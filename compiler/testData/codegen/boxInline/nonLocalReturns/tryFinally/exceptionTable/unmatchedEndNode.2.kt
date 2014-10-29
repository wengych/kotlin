package test

public class Holder(var value: String = "") {

    public fun plusAssign(s: String?) {
        if (value.length != 0) {
            value += " -> "
        }
        value += s
    }

    override fun toString(): String {
        return value
    }

}

public class Exception1(message: String) : java.lang.RuntimeException(message)

public class Exception2(message: String) : java.lang.RuntimeException(message)

public inline fun doCall(block: ()-> String, finallyBlock: ()-> String,
                         finallyBlock2: ()-> String, finallyBlock3: ()-> String, res: String = "Fail") : String {
    try {
        try {
            try {
                block()
            }
            finally {
                if (true) {
                    finallyBlock()
                    /*External finally would be injected here*/
                    return res + "_INNER_FINALLY"
                }
            }
        }
        finally {
            if (true) {
                finallyBlock2()
            }
        }
    } finally {
        finallyBlock3()
    }
    return res
}