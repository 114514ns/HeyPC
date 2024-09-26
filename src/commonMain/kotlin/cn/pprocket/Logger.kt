package cn.pprocket

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.toLocalDateTime

class Logger(var className: String) {


    private fun time(): String {
        val currentTime = Clock.System.now() // 获取当前时间
        val time = currentTime.toLocalDateTime(TimeZone.currentSystemDefault()).time


        return ""
    }

    fun info(message: String?) {
        println("[INFO] [${time()}] [%${className}] ${message}")
    }
    fun error(message: String?) {
        info(message)
    }


}
