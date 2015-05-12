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

package org.jetbrains.kotlin.idea.refactoring.move.moveTopLevelDeclarations.ui

import com.intellij.codeInsight.highlighting.HighlightUsagesDescriptionLocation
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.ElementDescriptionUtil
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiDirectory
import com.intellij.refactoring.RefactoringBundle
import com.intellij.refactoring.util.RadioUpDownListener
import com.intellij.refactoring.util.RefactoringDescriptionLocation
import com.intellij.util.containers.HashSet
import org.jetbrains.kotlin.psi.JetNamedDeclaration
import java.awt.BorderLayout
import javax.swing.*

public class MoveKotlinDeclarationOrFileDialog(
        project: Project,
        private val declarationToMove: JetNamedDeclaration
) : DialogWrapper(project, true) {
    private var rbMoveDeclaration = JRadioButton()
    private var rbMoveFile = JRadioButton()

    init {
        setTitle(RefactoringBundle.message("select.refactoring.title"))
        init()
    }

    override fun createNorthPanel() = JLabel(RefactoringBundle.message("what.would.you.like.to.do"))

    override fun getPreferredFocusedComponent() = rbMoveDeclaration

    override fun getDimensionServiceKey() = "#${javaClass.getName()}"

    override fun createCenterPanel(): JComponent? {
        val panel = JPanel(BorderLayout())

        val declarationDescription = ElementDescriptionUtil.getElementDescription(declarationToMove,
                                                                                  HighlightUsagesDescriptionLocation.INSTANCE)
        rbMoveDeclaration.setText("Move $declarationDescription to another file/package")
        rbMoveDeclaration.setSelected(true)

        rbMoveFile.setText("Move file ${declarationToMove.getContainingFile().getName()} to another package")

        val gr = ButtonGroup()
        gr.add(rbMoveDeclaration)
        gr.add(rbMoveFile)
        RadioUpDownListener(rbMoveDeclaration, rbMoveFile)

        val box = Box.createVerticalBox()
        box.add(Box.createVerticalStrut(5))
        box.add(rbMoveDeclaration)
        box.add(rbMoveFile)
        panel.add(box, BorderLayout.CENTER)

        return panel
    }

    public val moveFileSelected: Boolean
        get() = rbMoveFile.isSelected()
}