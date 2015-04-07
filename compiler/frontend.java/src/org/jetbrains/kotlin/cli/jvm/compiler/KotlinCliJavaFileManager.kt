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

package org.jetbrains.kotlin.cli.jvm.compiler

import com.intellij.core.CoreJavaFileManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.impl.file.PsiPackageImpl
import com.intellij.psi.impl.file.impl.JavaFileManager
import com.intellij.psi.search.GlobalSearchScope
import kotlin.Function1
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

import java.util.ArrayList
import kotlin.properties.Delegates

public class KotlinCliJavaFileManager(private val myPsiManager: PsiManager) : CoreJavaFileManager(myPsiManager) {

    private var packagesCache: JvmDependenciesIndex by Delegates.notNull()

    public fun initCache(packagesCache: JvmDependenciesIndex) {
        this.packagesCache = packagesCache
    }

    public fun findClass(classId: ClassId, scope: GlobalSearchScope): PsiClass? {
        val classNameWithInnerClasses = classId.getRelativeClassName().asString()
        return packagesCache.findClass(classId) { dir, type ->
            findClassGivenPackage(scope, dir, classNameWithInnerClasses, type)
        }
    }

    override fun findClass(qName: String, scope: GlobalSearchScope): PsiClass? {
        // this method is called from IDEA to resolve dependencies in Java code
        // which supposedly should compile so the dependencies exist in general
        // Most classes are top level classes so we will try to find them fast
        // but we must sometimes fallback to support finding inner/nested classes
        return qName.toSafeTopLevelClassId()?.let { classId -> findClass(classId, scope) }
               ?: super.findClass(qName, scope)
    }

    override fun findClasses(qName: String, scope: GlobalSearchScope): Array<PsiClass> {
        val classIdAsTopLevelClass = qName.toSafeTopLevelClassId() ?: return super.findClasses(qName, scope)

        val result = ArrayList<PsiClass>()
        val classNameWithInnerClasses = classIdAsTopLevelClass.getRelativeClassName().asString()
        packagesCache.traverseDirectoriesInPackage(classIdAsTopLevelClass.getPackageFqName()) { dir, rootType ->
            val psiClass = findClassGivenPackage(scope, dir, classNameWithInnerClasses, rootType)
            if (psiClass != null) {
                result.add(psiClass)
            }
            // traverse all
            true
        }
        if (result.isEmpty()) {
            return super.findClasses(qName, scope)
        }
        return result.toArray<PsiClass>(arrayOfNulls<PsiClass>(result.size()))
    }

    public fun findClassGivenPackage(
            scope: GlobalSearchScope, packageDir: VirtualFile,
            classNameWithInnerClasses: String, rootType: JavaRoot.RootType
    ): PsiClass? {
        val topLevelClassName = classNameWithInnerClasses.substringBefore('.')

        val vFile = when (rootType) {
            JavaRoot.RootType.BINARY -> packageDir.findChild("$topLevelClassName.class")
            JavaRoot.RootType.SOURCE -> packageDir.findChild("$topLevelClassName.java")
        }

        if (vFile == null) return null

        if (!vFile.isValid()) {
            LOG.error("Invalid child of valid parent: ${vFile.getPath()}; ${packageDir.isValid()} path=${packageDir.getPath()}")
            return null
        }
        if (vFile !in scope) {
            return null
        }

        val file = myPsiManager.findFile(vFile) as? PsiClassOwner ?: return null
        return findClassInPsiFile(classNameWithInnerClasses, file)
    }

    override fun findPackage(packageName: String): PsiPackage? {
        var found = false
        packagesCache.traverseDirectoriesInPackage(FqName(packageName)) { _, __ ->
            found = true
            //abort on first found
            false
        }
        if (found) {
            return PsiPackageImpl(myPsiManager, packageName)
        }
        return null
    }

    companion object {
        private val LOG = Logger.getInstance(javaClass<KotlinCliJavaFileManager>())

        private fun findClassInPsiFile(classNameWithInnerClassesDotSeparated: String, file: PsiClassOwner): PsiClass? {
            for (topLevelClass in file.getClasses()) {
                val candidate = findClassByTopLevelClass(classNameWithInnerClassesDotSeparated, topLevelClass)
                if (candidate != null) {
                    return candidate
                }
            }
            return null
        }

        private fun findClassByTopLevelClass(className: String, topLevelClass: PsiClass): PsiClass? {
            if (className.indexOf('.') < 0) {
                return if (className == topLevelClass.getName()) topLevelClass else null
            }

            val segments = StringUtil.split(className, ".").iterator()
            if (!segments.hasNext() || segments.next() != topLevelClass.getName()) {
                return null
            }
            var curClass = topLevelClass
            while (segments.hasNext()) {
                val innerClassName = segments.next()
                val innerClass = curClass.findInnerClassByName(innerClassName, false)
                if (innerClass == null) {
                    return null
                }
                curClass = innerClass
            }
            return curClass
        }
    }
}

// a sad workaround to avoid throwing exception when called from inside IDEA code
private fun String.toSafeTopLevelClassId(): ClassId? = try {
    ClassId.topLevel(FqName(this))
}
catch (e: Exception) {
    null
}
