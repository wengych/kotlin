fun main() {
    1.toByte().toString()
    1.toShort().toString()
    1.toString()
    1L.toString()
    1.0F.toString()
    1.0.toString()
    'c'.toString()
}

/*Check that all "valueOf" are String ones and there is no boxing*/
// 7 valueOf
// 7 INVOKESTATIC java/lang/String.valueOf
