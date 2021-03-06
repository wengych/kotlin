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

package org.jetbrains.kotlin.idea.refactoring.move.changePackage

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.PackageWrapper
import org.jetbrains.kotlin.idea.codeInsight.shorten.runWithElementsToShortenIsEmptyIgnored
import org.jetbrains.kotlin.idea.refactoring.move.PackageNameInfo
import org.jetbrains.kotlin.idea.refactoring.move.getInternalReferencesToUpdateOnPackageNameChange
import org.jetbrains.kotlin.idea.refactoring.move.moveTopLevelDeclarations.KotlinMoveTarget
import org.jetbrains.kotlin.idea.refactoring.move.moveTopLevelDeclarations.MoveKotlinTopLevelDeclarationsOptions
import org.jetbrains.kotlin.idea.refactoring.move.moveTopLevelDeclarations.MoveKotlinTopLevelDeclarationsProcessor
import org.jetbrains.kotlin.idea.refactoring.move.moveTopLevelDeclarations.Mover
import org.jetbrains.kotlin.idea.refactoring.move.postProcessMoveUsages
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.JetFile
import org.jetbrains.kotlin.psi.JetNamedDeclaration

public class KotlinChangePackageRefactoring(val file: JetFile) {
    private val project = file.getProject()

    fun run(newFqName: FqName) {
        val packageDirective = file.getPackageDirective() ?: return
        val currentFqName = packageDirective.getFqName()

        val declarationProcessor = MoveKotlinTopLevelDeclarationsProcessor(
                project,
                MoveKotlinTopLevelDeclarationsOptions(
                        elementsToMove = file.getDeclarations().filterIsInstance<JetNamedDeclaration>(),
                        moveTarget = object: KotlinMoveTarget {
                            override val packageWrapper = PackageWrapper(file.getManager(), newFqName.asString())

                            override fun getOrCreateTargetPsi(originalPsi: PsiElement) = originalPsi.getContainingFile()

                            override fun getTargetPsiIfExists(originalPsi: PsiElement) = null

                            override fun verify(file: PsiFile) = null
                        },
                        updateInternalReferences = false
                ),
                Mover.Idle // we don't need to move any declarations physically
        )

        val declarationUsages = declarationProcessor.findUsages().toList()
        val internalUsages = file.getInternalReferencesToUpdateOnPackageNameChange(PackageNameInfo(currentFqName, newFqName))

        project.executeWriteCommand("Change file's package to '${newFqName.asString()}'") {
            postProcessMoveUsages(internalUsages)
            packageDirective.setFqName(newFqName)
            project.runWithElementsToShortenIsEmptyIgnored { declarationProcessor.execute(declarationUsages) }
        }
    }
}
