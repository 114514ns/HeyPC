package cn.pprocket.ui


expect object PlatformU {
    fun isFullScreen(): Boolean
    fun openImage(url: String)
    fun read(name: String):String
    fun createFile(name: String)
    fun saveFile(name: String, content: String)
    fun containFile(name: String): Boolean
}
