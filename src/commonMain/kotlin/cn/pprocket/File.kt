package cn.pprocket

import cn.pprocket.ui.PlatformU

class File(var name: String = "") {


    fun read(): String {
        return PlatformU.read(name)
    }

    fun write(content: String) {
        PlatformU.saveFile(name, content)
    }

}
