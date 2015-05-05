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

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.jetbrains.kotlin.lexer.JetTokens
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.stubs.KotlinEnumReferenceExpressionStub
import org.jetbrains.kotlin.psi.stubs.elements.JetStubElementTypes

// This node represents "fake" reference expression for ENUM_ENTRY(arguments) constructor syntax
// It uses the owner enum node to provide access to the real constructor name
public class JetEnumReferenceExpression : JetExpressionImplStub<KotlinEnumReferenceExpressionStub>, JetSimpleNameExpression {

    // It is the owner enum class (not an enum entry but the whole enum)
    private val referencedElement: JetClass

    private fun calcReferencedElement(): JetClass? {
        var owner: PsiElement? = this
        do {
            owner = owner?.getParent()
        } while (owner != null && owner !is JetEnumEntry)
        return ((owner as? JetEnumEntry)?.getParent()?.getParent() as? JetClass)
    }

    public constructor(node: ASTNode) : super(node) {
        referencedElement = calcReferencedElement()!!
    }

    public constructor(stub: KotlinEnumReferenceExpressionStub) : super(stub, JetStubElementTypes.ENUM_REFERENCE_EXPRESSION) {
        referencedElement = calcReferencedElement()!!
    }

    override fun getReferencedName(): String {
        val stub = getStub()
        if (stub != null) {
            return stub.getReferencedName()
        }
        val text = this.getReferencedNameElement().getNode()!!.getText()
        return JetPsiUtil.unquoteIdentifierOrFieldReference(text)
    }

    override fun getReferencedNameAsName(): Name {
        return Name.identifierNoValidate(referencedElement.getName() ?: "")
    }

    override fun getReferencedNameElement(): PsiElement {
        return referencedElement
    }

    override fun getIdentifier(): PsiElement? {
        return referencedElement.getNameIdentifier()
    }

    override fun getReferencedNameElementType(): IElementType {
        return this.getReferencedNameElement().getNode()!!.getElementType()
    }

    override fun <R, D> accept(visitor: JetVisitor<R, D>, data: D): R {
        return visitor.visitSimpleNameExpression(this, data)
    }
}
