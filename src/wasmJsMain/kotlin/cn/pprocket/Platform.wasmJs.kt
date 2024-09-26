package cn.pprocket.ui

import kotlinx.browser.window
import org.w3c.dom.Window
import org.w3c.dom.get
import org.w3c.dom.set

actual object PlatformU {
    actual fun isFullScreen(): Boolean {
        return true
    }

    actual fun openImage(url: String) {
        window.open(url)
    }

    actual fun read(name: String): String {
        return window.localStorage[name]!!
    }

    actual fun createFile(name: String) {
        window.localStorage[name] = ""
    }

    actual fun saveFile(name: String, content: String) {
    }

    actual fun containFile(name: String): Boolean {
        return window.localStorage[name]!= null
    }
}
