package cn.pprocket.ui

import android.app.Application
import androidx.compose.material3.Typography
import com.google.firebase.FirebasePlatform
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import java.awt.Desktop
import java.net.URI

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
        FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
            val storage = mutableMapOf<String, String>()
            override fun clear(key: String) {
                storage.remove(key)
            }

            override fun log(msg: String) = println(msg)

            override fun retrieve(key: String) = storage[key]

            override fun store(key: String, value: String) = storage.set(key, value)
        })
        val options = FirebaseOptions(
            applicationId = "1:842179786357:android:d8938731447386c989ea3c",
            apiKey = "AIzaSyCGyaVawlhpHbp9OI5xeVYOpWP2XPxQKRM",
            projectId = "pphub-a16cf",
        )

        //val app = Firebase.initialize(Application(), options)
    }

}
