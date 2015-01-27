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

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.JetNodeTypes;
import org.jetbrains.kotlin.lexer.JetTokens;

public class JetCallableReferenceExpression extends JetExpressionImpl {
    public JetCallableReferenceExpression(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    public JetTypeReference getTypeReference() {
        return (JetTypeReference) findChildByType(JetNodeTypes.TYPE_REFERENCE);
    }

    @NotNull
    public PsiElement getDoubleColonTokenReference() {
        //noinspection ConstantConditions
        return findChildByType(JetTokens.COLONCOLON);
    }

    @NotNull
    public JetSimpleNameExpression getCallableReference() {
        PsiElement psi = getDoubleColonTokenReference();
        while (psi != null) {
            if (psi instanceof JetSimpleNameExpression) {
                return (JetSimpleNameExpression) psi;
            }
            psi = psi.getNextSibling();
        }

        throw new IllegalStateException("Callable reference simple name shouldn't be parsed to null");
    }

    @Override
    public <R, D> R accept(@NotNull JetVisitor<R, D> visitor, D data) {
        return visitor.visitCallableReferenceExpression(this, data);
    }
}