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

package kotlin.js.dom.html5

import kotlin.js.dom.html.HTMLElement
import kotlin.js.native

public native trait TimeRanges {
    val length : Long

    fun start(index : Long) : Double
    fun end(index : Long) : Double
}

public native trait HTMLMediaElement : HTMLElement {
    var autoplay : Boolean
    val buffered : TimeRanges
    var controls : Boolean
    val currentSrc : String
    val currentTime : Double

    var defaultMuted : Boolean
    var muted : Boolean

    var defaultPlaybackRate : Double
    val duration : Double
    val ended : Boolean
    val error : Any?
    var loop : Boolean
    var mediaGroup : String

    var networkState : Short
    val paused : Boolean
    var playbackRate : Double
    var played : TimeRanges
    var preload : String

    val readyState : Short
    val seekable : TimeRanges
    val seeking : Boolean

    var src : String
    var volume : Double

    fun canPlayType(type : String) : String
    fun fastSeek(time : Double)
    fun load()

    fun pause()
    fun play()

    var onabort : () -> Unit
    var oncanplay : () -> Unit
    var oncanplaythrough : () -> Unit
    var ondurationchange : () -> Unit
    var onemptied : () -> Unit
    var onended : () -> Unit
    var onerror : () -> Unit
    var oninterruptbegin : () -> Unit
    var oninterruptend : () -> Unit
    var onloadeddata : () -> Unit
    var onloadedmetadata : () -> Unit
    var onloadstart : () -> Unit
    var onpause : () -> Unit
    var onplay : () -> Unit
    var onplaying : () -> Unit
    var onprogress : () -> Unit
    var onratechange : () -> Unit
    var onseeked : () -> Unit
    var onseeking : () -> Unit
    var onstalled : () -> Unit
    var onsuspend : () -> Unit
    var ontimeupdate : () -> Unit
    var onvolumechange : () -> Unit
    var onwaiting : () -> Unit
}

public native trait HTMLAudioElement : HTMLMediaElement {
}

public native trait HTMLVideoElement : HTMLMediaElement {
    var width : String
    var height : String

    val videoWidth : Long
    val videoHeight : Long

    val poster : String
}