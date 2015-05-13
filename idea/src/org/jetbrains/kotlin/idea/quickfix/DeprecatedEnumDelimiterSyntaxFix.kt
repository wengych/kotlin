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

package org.jetbrains.kotlin.idea.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.quickfix.quickfixUtil.createIntentionFactory
import org.jetbrains.kotlin.idea.quickfix.quickfixUtil.createIntentionForFirstParentOfType
import org.jetbrains.kotlin.lexer.JetTokens
import org.jetbrains.kotlin.psi
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.psi.psiUtil.getNextSiblingIgnoringWhitespace
import org.jetbrains.kotlin.psi.stubs.elements.JetStubElementTypes
import org.jetbrains.kotlin.resolve.DeclarationsChecker

class DeprecatedEnumDelimiterSyntaxFix(element: JetEnumEntry): JetIntentionAction<JetEnumEntry>(element) {

    override fun getFamilyName(): String = "Insert lacking comma(s) / semicolon(s)"

    override fun getText(): String = "Insert lacking comma(s) / semicolon(s)"

    override fun invoke(project: Project, editor: Editor?, file: JetFile?) = insertLackingCommaSemicolon(element)

    companion object : JetSingleIntentionActionFactory() {
        override fun createAction(diagnostic: Diagnostic): IntentionAction? =
                diagnostic.createIntentionForFirstParentOfType(::DeprecatedEnumDelimiterSyntaxFix)

        public fun createWholeProjectFixFactory(): JetSingleIntentionActionFactory = createIntentionFactory {
            JetWholeProjectForEachElementOfTypeFix.createByPredicate<JetEnumEntry>(
                    predicate = { DeclarationsChecker.enumEntryUsesDeprecatedOrNoDelimiter(it) },
                    taskProcessor = { insertLackingCommaSemicolon(it) },
                    modalTitle = "Replacing deprecated enum delimiter syntax",
                    name = "Insert lacking comma(s) / semicolon(s)",
                    familyName = "Insert lacking comma(s) / semicolon(s)"
            )
        }

        private fun insertLackingCommaSemicolon(enumEntry: JetEnumEntry) {
            val klass = enumEntry.getParent().getParent() as JetClass
            assert(klass.isEnum(), "This quick fix is intended to work with enums only")
            val body = klass.getBody()!!
            val entries = body.getChildrenOfType<JetEnumEntry>()
            val psiFactory = psi.JetPsiFactory(body)
            var entryIndex = 0
            for (entry in entries) {
                entryIndex++
                var next = entry.getNextSiblingIgnoringWhitespace()
                var nextType = next.getNode().getElementType()
                if (entryIndex < entries.size()) {
                    if (nextType != JetTokens.COMMA) {
                        body.addAfter(psiFactory.createComma(), entry)
                    }
                }
                else {
                    val prev: PsiElement
                    if (nextType == JetTokens.COMMA) {
                        prev = next
                        next = next.getNextSiblingIgnoringWhitespace()
                        nextType = next.getNode().getElementType()
                    }
                    else {
                        prev = entry
                    }
                    if (nextType != JetTokens.SEMICOLON && nextType != JetTokens.RBRACE) {
                        body.addAfter(psiFactory.createSemicolon(), prev)
                    }
                }
            }
        }
    }
}