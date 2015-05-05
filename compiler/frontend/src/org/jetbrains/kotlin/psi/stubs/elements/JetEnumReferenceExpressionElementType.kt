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

package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.util.io.StringRef
import org.jetbrains.annotations.NonNls
import org.jetbrains.kotlin.psi.JetEnumReferenceExpression
import org.jetbrains.kotlin.psi.stubs.KotlinEnumReferenceExpressionStub
import org.jetbrains.kotlin.psi.stubs.impl.KotlinEnumReferenceExpressionStubImpl
import java.io.IOException

public class JetEnumReferenceExpressionElementType(NonNls debugName: String)
    : JetStubElementType<KotlinEnumReferenceExpressionStub, JetEnumReferenceExpression>(
        debugName, javaClass<JetEnumReferenceExpression>(), javaClass<KotlinEnumReferenceExpressionStub>()) {

    override fun createStub(psi: JetEnumReferenceExpression, parentStub: StubElement<*>): KotlinEnumReferenceExpressionStub {
        return KotlinEnumReferenceExpressionStubImpl(parentStub, StringRef.fromString(psi.getReferencedName()))
    }

    throws(IOException::class)
    override fun serialize(stub: KotlinEnumReferenceExpressionStub, dataStream: StubOutputStream) {
        dataStream.writeName(stub.getReferencedName())
    }

    throws(IOException::class)
    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>): KotlinEnumReferenceExpressionStub {
        val referencedName = dataStream.readName()
        return KotlinEnumReferenceExpressionStubImpl(parentStub, referencedName)
    }
}
