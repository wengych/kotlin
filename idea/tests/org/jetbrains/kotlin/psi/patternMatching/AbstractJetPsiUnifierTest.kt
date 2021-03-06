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

package org.jetbrains.kotlin.psi.patternMatching

import org.jetbrains.kotlin.idea.test.JetLightCodeInsightFixtureTestCase
import com.intellij.testFramework.LightProjectDescriptor
import org.jetbrains.kotlin.psi.JetFile
import org.jetbrains.kotlin.psi.psiUtil.parents
import com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.psi.JetElement
import org.jetbrains.kotlin.idea.util.psi.patternMatching.JetPsiUnifier
import org.jetbrains.kotlin.idea.util.psi.patternMatching.toRange
import java.io.File
import org.jetbrains.kotlin.test.JetTestUtils
import org.jetbrains.kotlin.psi.JetExpression
import org.jetbrains.kotlin.psi.JetTypeReference
import org.jetbrains.kotlin.psi.JetWhenCondition
import org.jetbrains.kotlin.idea.test.DirectiveBasedActionUtils

public abstract class AbstractJetPsiUnifierTest: JetLightCodeInsightFixtureTestCase() {
    public fun doTest(filePath: String) {
        fun findPattern(file: JetFile): JetElement {
            val selectionModel = myFixture.getEditor().getSelectionModel()
            val start = selectionModel.getSelectionStart()
            val end = selectionModel.getSelectionEnd()
            val selectionRange = TextRange(start, end)
            return file.findElementAt(start)?.parents()?.last {
                (it is JetExpression || it is JetTypeReference || it is JetWhenCondition)
                        && selectionRange.contains(it.getTextRange() ?: TextRange.EMPTY_RANGE)
            } as JetElement
        }

        myFixture.configureByFile(filePath)
        val file = myFixture.getFile() as JetFile

        DirectiveBasedActionUtils.checkForUnexpectedErrors(file)

        val actualText =
                findPattern(file)
                        .toRange()
                        .match(file, JetPsiUnifier.DEFAULT)
                        .map { it.range.getTextRange().substring(file.getText()!!) }
                        .joinToString("\n\n")
        JetTestUtils.assertEqualsToFile(File("$filePath.match"), actualText)
    }

    override fun getProjectDescriptor(): LightProjectDescriptor = getProjectDescriptorFromTestName()
}
