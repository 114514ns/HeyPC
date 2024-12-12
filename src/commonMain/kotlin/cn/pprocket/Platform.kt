package cn.pprocket.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography


expect object PlatformU {
    fun isFullScreen(): Boolean
    fun openImage(url: String)
    fun read(name: String):String
    fun createFile(name: String)
    fun saveFile(name: String, content: String)
    fun containFile(name: String): Boolean
    fun getTypography() : Typography
    fun getPlatform():String
    fun setFullscreen(fullscreen:Boolean)

}
