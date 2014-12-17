/*
 * Copyright 2010-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.resolve.typeApproximation

public class CapturedTypeApproximationTestImpl : AbstractCapturedTypeApproximationTest() {
    override fun getTestDataPath() = "compiler/testData/capturedTypeApproximation/"

    public fun testSimpleT() {
        doTest("simpleT.txt", "T");
    }

    public fun testNullableT() {
        doTest("nullableT.txt", "T?")
    }

    public fun testUseSiteInT() {
        doTest("useSiteInT.txt", "in T");
    }

    public fun testUseSiteInNullableT() {
        doTest("useSiteInNullableT.txt", "in T?");
    }

    public fun testUseSiteOutT() {
        doTest("useSiteOutT.txt", "out T");
    }

    public fun testUseSiteOutNullableT() {
        doTest("useSiteOutNullableT.txt", "out T?");
    }

    public fun testTwoVariables() {
        doTest("twoVariables.txt", "T", "R")
    }
}
