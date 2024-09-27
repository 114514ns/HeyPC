package cn.pprocket.ui

import androidx.compose.material3.Typography
import kotlinx.browser.window
import org.w3c.dom.get
import org.w3c.dom.set

actual object PlatformU {

    var typography = Typography()

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



    actual fun getTypography(): Typography {
        return typography
    }

}
