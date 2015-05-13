/*
 * Generated file
 * DO NOT EDIT
 * 
 * See libraries/tools/idl2k for details
 */

package org.w3c.notifications

import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.css.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.workers.*
import org.w3c.xhr.*

native public open class Notification(title: String, options: NotificationOptions = noImpl) : EventTarget {
    var permission: String
        get() = noImpl
        set(value) = noImpl
    var onclick: (Event) -> Unit
        get() = noImpl
        set(value) = noImpl
    var onerror: (Event) -> Unit
        get() = noImpl
        set(value) = noImpl
    var title: String
        get() = noImpl
        set(value) = noImpl
    var dir: String
        get() = noImpl
        set(value) = noImpl
    var lang: String
        get() = noImpl
        set(value) = noImpl
    var body: String
        get() = noImpl
        set(value) = noImpl
    var tag: String
        get() = noImpl
        set(value) = noImpl
    var icon: String
        get() = noImpl
        set(value) = noImpl
    var sound: String
        get() = noImpl
        set(value) = noImpl
    var renotify: Boolean
        get() = noImpl
        set(value) = noImpl
    var silent: Boolean
        get() = noImpl
        set(value) = noImpl
    var noscreen: Boolean
        get() = noImpl
        set(value) = noImpl
    var sticky: Boolean
        get() = noImpl
        set(value) = noImpl
    var data: Any?
        get() = noImpl
        set(value) = noImpl
    fun close(): Unit = noImpl
}

native public open class NotificationOptions {
    var dir: String = "auto"
    var lang: String = ""
    var body: String = ""
    var tag: String = ""
    var icon: String
    var sound: String
    var vibrate: dynamic
    var renotify: Boolean = false
    var silent: Boolean = false
    var noscreen: Boolean = false
    var sticky: Boolean = false
    var data: Any? = null
}

native public trait NotificationPermissionCallback {
}

native public open class GetNotificationOptions {
    var tag: String = ""
}

native public open class NotificationEvent(type: String, eventInitDict: NotificationEventInit = noImpl) : ExtendableEvent(type, eventInitDict) {
    var notification: Notification
        get() = noImpl
        set(value) = noImpl
}

native public open class NotificationEventInit : ExtendableEventInit() {
    var notification: Notification
}

native public marker trait UnionElementOrHTMLCollection {
}

native public marker trait UnionElementOrRadioNodeList {
}

native public marker trait UnionHTMLOptGroupElementOrHTMLOptionElement {
}

native public marker trait UnionAudioTrackOrTextTrackOrVideoTrack {
}

native public marker trait UnionElementOrMouseEvent {
}

native public marker trait UnionElementOrProcessingInstruction {
}

native public marker trait UnionMessagePortOrServiceWorker {
}

native public marker trait UnionClientOrMessagePortOrServiceWorker {
}

