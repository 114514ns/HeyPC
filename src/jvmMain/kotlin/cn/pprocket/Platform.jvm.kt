package cn.pprocket.ui

import androidx.compose.material3.Typography
import java.awt.Desktop
import java.net.URI
import javax.sound.sampled.AudioSystem

actual object PlatformU {

    var mTypography = Typography()

    var isFull = false

    actual fun isFullScreen(): Boolean {

        //return GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.fullScreenWindow != null
        return isFull
    }

    actual fun openImage(url: String) {



        Thread {
            Desktop.getDesktop().browse(URI.create(url))
        }.start()
    }

    actual fun read(name: String) : String{
        val file = java.io.File(name)
        return file.readText()
    }

    actual fun createFile(name: String) {
        val file = java.io.File(name)
        file.createNewFile()
    }

    actual fun saveFile(name: String, content: String) {
        val file = java.io.File(name)
        file.writeText(content)
    }

    actual fun containFile(name: String): Boolean {
        val file = java.io.File(name)
        return file.exists()
    }

    actual fun getTypography(): Typography {
        return mTypography
    }

    actual fun getPlatform(): String {
        return "Desktop"
    }

    actual fun setFullscreen(fullscreen: Boolean) {
    }

    actual fun initFirebase() {

    }

}
