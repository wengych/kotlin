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

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.IntArrayList
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import java.util.ArrayList
import java.util.EnumSet
import java.util.HashMap
import kotlin.properties.Delegates

//TODO_R: not public?
public data class JavaRoot(public val file: VirtualFile, public val type: JavaRoot.RootType) {
    //TODO_R: constant enum sets
    public enum class RootType {
        SOURCE
        BINARY
    }
}

class JvmDependenciesIndex(private val roots: List<JavaRoot>) {
    //these fields are computed based on roots field which is filled in later
    private val maxIndex: Int by Delegates.lazy { roots.size() }

    private val rootCache: Cache by Delegates.lazy {
        with(Cache()) {
            roots.indices.forEach {
                rootIndices.add(it)
            }
            rootIndices.add(maxIndex)
            rootIndices.trimToSize()
            this
        }
    }

    private var searchCache: ClassSearchCache? = null

    fun <T : Any> findClass(
            classId: ClassId,
            acceptedRootTypes: Set<JavaRoot.RootType> = EnumSet.allOf(javaClass<JavaRoot.RootType>()),
            findClassGivenDirectory: (VirtualFile, JavaRoot.RootType) -> T?
    ): T? {
        return search(FindClassRequest(classId, acceptedRootTypes)) { dir, rootType ->
            val found = findClassGivenDirectory(dir, rootType)
            HandleResult(found, continueSearch = found == null)
        }
    }

    fun traverseDirectoriesInPackage(
            packageFqName: FqName,
            acceptedRootTypes: Set<JavaRoot.RootType> = EnumSet.allOf(javaClass<JavaRoot.RootType>()),
            continueSearch: (VirtualFile, JavaRoot.RootType) -> Boolean
    ): Unit {
        search(TraverseRequest(packageFqName, acceptedRootTypes)) { dir, rootType ->
            HandleResult(Unit, continueSearch(dir, rootType))
        }
    }

    private data class HandleResult<T: Any>(val result: T?, val continueSearch: Boolean)

    private fun <T : Any> search(
            request: SearchRequest,
            handler: (VirtualFile, JavaRoot.RootType) -> HandleResult<T>
    ): T? {

        fun doSearch() = doSearch(request, handler)

        if (request !is FindClassRequest || searchCache == null) {
            return doSearch()
        }
        val (cachedRequest, cachedResult) = searchCache!!
        if (cachedRequest.classId != request.classId) {
            return doSearch()
        }
        when (cachedResult) {
            is SearchResult.NotFound -> {
                val limitedRootTypes = request.acceptedRootTypes.toHashSet()
                limitedRootTypes.removeAll(cachedRequest.acceptedRootTypes)
                if (limitedRootTypes.isEmpty()) {
                    return null
                }
                else {
                    return doSearch(FindClassRequest(request.classId, limitedRootTypes), handler)
                }
            }
            is SearchResult.Found -> {
                if (cachedRequest.acceptedRootTypes == request.acceptedRootTypes) {
                    return handler(cachedResult.packageDirectory, cachedResult.root.type).result
                }
            }
        }

        return doSearch()
    }

    private fun <T : Any> doSearch(request: SearchRequest, handler: (VirtualFile, JavaRoot.RootType) -> HandleResult<T>): T? {
        val findClassRequest = request as? FindClassRequest

        fun <T : Any> found(packageDirectory: VirtualFile, root: JavaRoot, result: T): T {
            if (findClassRequest != null) {
                searchCache = ClassSearchCache(findClassRequest, SearchResult.Found(packageDirectory, root))
            }
            return result
        }

        fun <T : Any> notFound(): T? {
            if (findClassRequest != null) {
                searchCache = ClassSearchCache(findClassRequest, SearchResult.NotFound)
            }
            return null
        }

        fun handle(root: JavaRoot, targetDirInRoot: VirtualFile): T? {
            if (root.type in request.acceptedRootTypes) {
                val (result, shouldContinue) = handler(targetDirInRoot, root.type)
                if (!shouldContinue) {
                    return result
                }
            }
            return null
        }

        val packagesPath = request.packageFqName.pathSegments().map { it.getIdentifier() }
        val caches = cachesPath(packagesPath)

        var lastMaxRootIndex = -1
        for (cacheIndex in (caches.size() - 1) downTo 0) {
            val cache = caches[cacheIndex]
            for (i in cache.rootIndices.size().indices) {
                val rootIndex = cache.rootIndices[i]
                if (rootIndex <= lastMaxRootIndex) continue

                val dir = travelPath(rootIndex, packagesPath, cacheIndex, caches) ?: continue
                val root = roots[rootIndex]
                val result = handle(root, dir)
                if (result != null) {
                    return found(dir, root, result)
                }
            }
            lastMaxRootIndex = cache.rootIndices.lastOrNull() ?: lastMaxRootIndex
        }
        return notFound()
    }

    /**
     * root -> "org" -> "jet" -> "language"
     * [org, jet, language]
     */
    private fun travelPath(rootIndex: Int, packagesPath: List<String>, fillCachesAfter: Int, cachesPath: List<Cache>): VirtualFile? {
        if (rootIndex >= maxIndex) {
            for (i in (fillCachesAfter + 1)..cachesPath.size() - 1) {
                cachesPath[i].rootIndices.add(maxIndex)
                cachesPath[i].rootIndices.trimToSize()
            }
            return null
        }

        var currentFile = roots[rootIndex].file
        for (pathIndex in packagesPath.indices) {
            currentFile = currentFile.findChild(packagesPath[pathIndex]) ?: return null
            val correspondingCacheIndex = pathIndex + 1
            if (correspondingCacheIndex > fillCachesAfter) {
                cachesPath[correspondingCacheIndex].rootIndices.add(rootIndex)
            }
        }
        return currentFile
    }

    private fun cachesPath(path: List<String>): ArrayList<Cache> {
        val caches = ArrayList<Cache>()
        caches.add(rootCache)
        var currentCache = rootCache
        for (subPackageName in path) {
            currentCache = currentCache[subPackageName]
            caches.add(currentCache)
        }
        return caches
    }

    private class Cache {
        private val innerCaches = HashMap<String, Cache>()

        fun get(name: String) = innerCaches.getOrPut(name) { Cache() }

        val rootIndices = IntArrayList()
    }

    private data class ClassSearchCache(val request: FindClassRequest, val result: SearchResult)

    private data class FindClassRequest(val classId: ClassId, override val acceptedRootTypes: Set<JavaRoot.RootType>): SearchRequest {
        override val packageFqName: FqName
            get() = classId.getPackageFqName()
    }

    private data class TraverseRequest(
            override val packageFqName: FqName,
            override val acceptedRootTypes: Set<JavaRoot.RootType>
    ): SearchRequest

    private trait SearchRequest {
        val packageFqName: FqName
        val acceptedRootTypes: Set<JavaRoot.RootType>
    }

    trait SearchResult {
        class Found(val packageDirectory: VirtualFile, val root: JavaRoot) : SearchResult

        object NotFound : SearchResult
    }
}

fun IntArrayList.lastOrNull() = if (isEmpty()) null else get(size() - 1)