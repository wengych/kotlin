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

package org.jetbrains.idl2k.dl

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.parser.Tag
import java.io.File
import java.net.URL

val urls = listOf(
        "https://raw.githubusercontent.com/whatwg/html-mirror/master/source" to "org.w3c.dom",
        "https://html.spec.whatwg.org/" to "org.w3c.dom",
        "https://raw.githubusercontent.com/whatwg/dom/master/dom.html" to "org.w3c.dom",
        "http://www.w3.org/TR/uievents/" to "org.w3c.dom.events",
        "https://dvcs.w3.org/hg/editing/raw-file/tip/editing.html" to "org.w3c.dom",
        "https://raw.githubusercontent.com/whatwg/xhr/master/Overview.src.html" to "org.w3c.xhr",
        "https://raw.githubusercontent.com/w3c/FileAPI/gh-pages/index.html" to "org.w3c.files",
        "https://raw.githubusercontent.com/whatwg/notifications/master/notifications.html" to "org.w3c.notifications",
        "https://raw.githubusercontent.com/whatwg/fullscreen/master/Overview.src.html" to "org.w3c.fullscreen",
        "http://www.w3.org/TR/DOM-Parsing/" to "org.w3c.dom.parsing",
        "http://slightlyoff.github.io/ServiceWorker/spec/service_worker/index.html" to "org.w3c.workers",
        "https://raw.githubusercontent.com/whatwg/fetch/master/Overview.src.html" to "org.w3c.fetch",
        "http://www.w3.org/TR/vibration/" to "org.w3c.vibration",
        "http://dev.w3.org/csswg/cssom/" to "org.w3c.dom.css",
        "https://www.khronos.org/registry/webgl/specs/latest/1.0/webgl.idl" to "org.khronos.webgl",
        "https://www.khronos.org/registry/typedarray/specs/latest/typedarray.idl" to "org.khronos.webgl"
)

fun main(args: Array<String>) {
    val dir = File("../../idl")
    dir.mkdirs()

    val urlsPerFiles = urls.groupBy { it.second + ".idl" }

    urlsPerFiles.forEach { e ->
        val fileName = e.key
        val pkg = e.value.first().second

        File(dir, fileName).bufferedWriter().use { w ->
            w.appendln("namespace ${pkg};")
            w.appendln()
            w.appendln()

            e.value.forEach { pair ->
                val (url, _) = pair
                println("Loading ${url}...")

                if (url.endsWith(".idl")) {
                    w.appendln(URL(url).readText())
                } else {
                    extractIDLText(url, w)
                }
            }

            w.appendln()
        }
    }
}

private fun extractIDLText(url: String, out: Appendable) {
    //    val soup = Jsoup.connect(url).validateTLSCertificates(false).ignoreHttpErrors(true).get()
    val soup = Jsoup.parse(URL(url).readText())
    fun append(it : Element) {
        if (!it.tag().preserveWhitespace()) {
            return append(Element(Tag.valueOf("pre"), it.baseUri()).appendChild(it))
        }

        val text = it.text()
        out.appendln(text)
        if (!text.trimEnd().endsWith(";")) {
            out.appendln(";")
        }
    }

    soup.select("pre.idl").filter {!it.hasClass("extract")}.forEach(::append)
    soup.select("code.idl-code").forEach(::append)
    soup.select("spec-idl").forEach(::append)
}
