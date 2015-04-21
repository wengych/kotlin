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

package kotlin.js.dom.walker

import org.w3c.dom.Node
import kotlin.js.dom.html.HTMLDocument
import kotlin.js.dom.html.HTMLElement
import kotlin.js.dom.html.document
import kotlin.support.AbstractIterator

enum class NodeFilterResult(val code: Short) {
    FILTER_ACCEPT : NodeFilterResult(NodeFilter.FILTER_ACCEPT)
    FILTER_REJECT : NodeFilterResult(NodeFilter.FILTER_REJECT)
    FILTER_SKIP : NodeFilterResult(NodeFilter.FILTER_SKIP)
}

fun nodeFilter(filter: (Node) -> NodeFilterResult) = object : NodeFilter {
    override val acceptNode: (Node) -> Short
        get() = { filter(it).code }
}

native trait NodeFilter {
    val acceptNode: (Node) -> Short

    companion object {
        val SHOW_ALL: Short
        val SHOW_ATTRIBUTE: Short
        val SHOW_CDATA_SECTION : Short
        val SHOW_COMMENT : Short
        val SHOW_DOCUMENT : Short
        val SHOW_DOCUMENT_FRAGMENT : Short
        val SHOW_DOCUMENT_TYPE : Short
        val SHOW_ELEMENT : Short
        val SHOW_PROCESSING_INSTRUCTION : Short
        val SHOW_TEXT : Short

        val FILTER_ACCEPT : Short
        val FILTER_REJECT : Short
        val FILTER_SKIP : Short
    }
}

public native trait TreeWalker {
    val root: Node
    val whatToShow: Short
    val filter: NodeFilter
    val currentNode: Node?

    fun parentNode() : Node?
    fun firstChild() : Node?
    fun lastChild() : Node?

    fun previousSibling() : Node?
    fun nextSibling() : Node?

    fun previousNode() : Node?
    fun nextNode() : Node?
}

public native fun HTMLDocument.createTreeWalker(root : HTMLElement) : TreeWalker
public native fun HTMLDocument.createTreeWalker(root : HTMLElement, whatToShow : Short) : TreeWalker
public native fun HTMLDocument.createTreeWalker(root : HTMLElement, whatToShow : Short, filter : (Node) -> Short) : TreeWalker

public fun HTMLDocument.createTreeWalkerK(root : HTMLElement, whatToShow : Short, filter : (Node) -> NodeFilterResult) : TreeWalker =
        createTreeWalker(root, whatToShow) { filter(it).code }

private class TreeWalkerIterator(val treeWalker : TreeWalker) : AbstractIterator<HTMLElement>() {
    override fun computeNext() {
        val next = treeWalker.nextNode()

        if (next == null) {
            done()
        } else {
            setNext(next as HTMLElement)
        }
    }
}

public fun HTMLElement.treeWalkerSequence() : Sequence<HTMLElement> = object : Sequence<HTMLElement> {
    override fun iterator(): Iterator<HTMLElement> = TreeWalkerIterator((ownerDocument as? HTMLDocument ?: document).createTreeWalker(this@treeWalkerSequence, NodeFilter.SHOW_ELEMENT))
}

