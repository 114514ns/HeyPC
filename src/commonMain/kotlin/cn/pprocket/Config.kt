package cn.pprocket

import cn.pprocket.items.User
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


object UserSerializer : KSerializer<User> {
    override val descriptor: SerialDescriptor
        get() = TODO("Not yet implemented")

    override fun deserialize(decoder: Decoder): User {
        return decoder.
    }

    override fun serialize(encoder: Encoder, value: User) {
        encoder.encodeString(value.toString())
    }
}

@Serializable
data class Config(
    var blackWords: List<String> = mutableListOf(),
    var cookies: String = "",
    var isLogin: Boolean = false,
    var user: User = User(),
    var blockCube: Boolean = false,
    var showTab: Boolean = false,
    var originImage: Boolean = true,
    var colorRed: Int = 0,
    var colorGreen: Int = 0,
    var colorBlue: Int = 0,
) {

}
