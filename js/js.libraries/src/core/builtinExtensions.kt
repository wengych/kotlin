/*
 * Copyright 2010-2015 JetBrains s.r.o.
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

package kotlin

public val Int.Companion.MAX_VALUE: Int
    get() = 0x7FFFFFFF

public val Int.Companion.MIN_VALUE: Int
    get() = -0x80000000


public val Long.Companion.MAX_VALUE: Long
    get() = js("Kotlin.Long.MAX_VALUE")

public val Long.Companion.MIN_VALUE: Long
    get() = js("Kotlin.Long.MIN_VALUE")
