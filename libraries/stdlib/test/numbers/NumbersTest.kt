package numbers

import org.junit.Test as test
import kotlin.test.*

class NumbersTest {


    test fun intMinMaxValues() {
        assertTrue(Int.MIN_VALUE < 0)
        assertTrue(Int.MAX_VALUE > 0)

        // doesn't hold for JS Number
        // assertEquals(Int.MIN_VALUE, Int.MAX_VALUE + 1)
    }

    test fun longMinMaxValues() {
        assertTrue(Long.MIN_VALUE < 0)
        assertTrue(Long.MAX_VALUE > 0)
        assertEquals(Long.MIN_VALUE, Long.MAX_VALUE + 1)
        assertEquals(Long.MAX_VALUE, Long.MIN_VALUE - 1)
    }

    test fun doubleProperties() {
        for (value in listOf(1.0, 0.0))
            doTestNumber(value)
        for (value in listOf(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY))
            doTestNumber(value, isInfinite = true)
        doTestNumber(Double.NaN, isNaN = true)
    }

    test fun floatProperties() {
        for (value in listOf(1.0F, 0.0F))
            doTestNumber(value)
        for (value in listOf(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY))
            doTestNumber(value, isInfinite = true)
        doTestNumber(Float.NaN, isNaN = true)
    }


    private fun doTestNumber(value: Double, isNaN: Boolean = false, isInfinite: Boolean = false) {
        assertEquals(isNaN, value.isNaN(), "Expected $value to have isNaN: $isNaN")
        assertEquals(isInfinite, value.isInfinite(), "Expected $value to have isInfinite: $isInfinite")
        assertEquals(!isNaN && !isInfinite, value.isFinite())
    }

    private fun doTestNumber(value: Float, isNaN: Boolean = false, isInfinite: Boolean = false) {
        assertEquals(isNaN, value.isNaN(), "Expected $value to have isNaN: $isNaN")
        assertEquals(isInfinite, value.isInfinite(), "Expected $value to have isInfinite: $isInfinite")
        assertEquals(!isNaN && !isInfinite, value.isFinite())
    }

}