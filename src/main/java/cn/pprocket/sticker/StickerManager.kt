package cn.pprocket.sticker

import cn.pprocket.pages.getStickerDir
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object StickerManager {
    val list = mutableListOf<Sticker>()
    val file = File("sticker.json")

    init {
        if (!file.exists()) {
            file.createNewFile()
        } else {
            val text = file.readText()
            list.addAll(Gson().fromJson(text, object : TypeToken<List<Sticker?>?>() {}.type))
        }
        if (!getStickerDir().exists()) {
            getStickerDir().mkdirs()
        }
    }

    fun add(sticker: Sticker) {
        list.add(sticker)
        save()
    }

    fun save() {
        val text = Gson().toJson(list)
        file.writeText(text)
    }

    fun delete(sticker: Sticker) {
        list.remove(sticker)
        save()
    }

    fun search(keyWord: String,exactly : Boolean): List<Sticker> {
        if (exactly) {
            return list.filter { it.keyword.equals(keyWord, true) }
        } else {
            return list.filter { it.keyword.contains(keyWord, true) }
        }

    }


}
