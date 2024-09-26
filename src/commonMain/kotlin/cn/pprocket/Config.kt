package cn.pprocket

import cn.pprocket.items.User
import kotlinx.serialization.Serializable


@Serializable
data class Config(
    var blackWords: List<String> = mutableListOf(),
    var cookies: String = "",
    var isLogin: Boolean = false,
    var user: User = User(),
    var blockCube: Boolean = true,
    var showTab: Boolean = false,
    var originImage: Boolean = true,
    var colorRed: Int = 0,
    var colorGreen: Int = 0,
    var colorBlue: Int = 0,
) {

}
