package kotlin.js.dom.selectors

import org.w3c.dom.DocumentFragment
import org.w3c.dom.NodeList
import kotlin.js.dom.html.HTMLDocument
import kotlin.js.dom.html.HTMLElement

public native fun HTMLDocument.querySelector(query : String) : HTMLElement?
public native fun HTMLDocument.querySelectorAll(query : String) : NodeList

public native fun HTMLElement.querySelector(query : String) : HTMLElement?
public native fun HTMLElement.querySelectorAll(query : String) : NodeList

public native fun DocumentFragment.querySelector(query : String) : HTMLElement?
public native fun DocumentFragment.querySelectorAll(query : String) : NodeList
