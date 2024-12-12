package cn.pprocket.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Typography
import androidx.compose.ui.platform.LocalConfiguration
import java.util.*

@SuppressLint("StaticFieldLeak")
actual object PlatformU {
    var mTypography = Typography()
    var mFullscreen = false
    var mContext: Context? = null
    actual fun isFullScreen(): Boolean {
        return mFullscreen
    }

    @RequiresApi(Build.VERSION_CODES.O)
    actual fun openImage(url: String) {

        val intent = Intent()
        intent.data = Uri.parse(url)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mContext?.startActivity(intent)
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
        return "Android"
    }

    actual fun setFullscreen(fullscreen: Boolean) {
        this.mFullscreen = fullscreen
    }

}