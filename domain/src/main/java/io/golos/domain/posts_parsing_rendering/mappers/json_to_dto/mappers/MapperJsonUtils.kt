package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import android.graphics.Color
import android.net.Uri
import android.util.Size
import io.golos.domain.use_cases.post.TextStyle
import io.golos.domain.posts_parsing_rendering.Attribute
import io.golos.domain.posts_parsing_rendering.BlockType
import org.json.JSONArray
import org.json.JSONObject
import java.lang.UnsupportedOperationException

abstract class MapperJsonUtils {
    protected fun JSONObject.getAttributes(): JSONObject? = this.tryJSONObject("attributes")

    protected fun JSONObject.getType(): BlockType = this.getString("type").let { type -> BlockType.values().first { it.value == type } }

    protected fun JSONArray?.contains(value: String): Boolean {
        if(this == null) {
            return false
        }

        if(this.length() == 0) {
            return false
        }

        for(i in 0 until this.length()) {
            if(this.getString(i) == value) {
                return true
            }
        }

        return false
    }

    protected fun JSONObject.tryTextStyle(attr: Attribute): TextStyle? =
        this.tryJSONArray(attr)
            ?.let {
                val styles = mutableListOf<String>()

                for(i in 0 until it.length()) {
                    styles.add(it.getString(i))
                }

                styles.mapStringToTextStyle()
            }

    protected fun JSONObject.tryColor(attr: Attribute): Int? = this.tryString(attr.value)?.let { Color.parseColor(it) }

    protected fun JSONObject.trySize(attr: Attribute): Size? = this.tryJSONArray(attr)?.let { Size(it.getInt(0), it.getInt(1)) }

    protected fun JSONObject.tryUri(attr: Attribute): Uri? = this.tryUri(attr.value)

    protected fun JSONObject.tryString(attr: Attribute): String? = this.tryString(attr.value)

    protected fun JSONObject.tryJSONObject(attr: Attribute): JSONObject? = this.tryJSONObject(attr.value)

    protected fun JSONObject.tryJSONArray(attr: Attribute): JSONArray? = this.tryJSONArray(attr.value)


    protected fun JSONObject.tryUri(name: String): Uri? = this.tryString(name)?.let { Uri.parse(it) }

    protected fun JSONObject.tryString(name: String): String? = this.takeIf { it.has(name) }?.getString(name)

    protected fun JSONObject.tryJSONObject(name: String): JSONObject? = this.optJSONObject(name)

    protected fun JSONObject.tryJSONArray(name: String): JSONArray? = this.optJSONArray(name)


    protected fun JSONObject.getLong(attr: Attribute): Long = this.getLong(attr.value)

    protected fun JSONObject.getString(attr: Attribute): String = this.getString(attr.value)

    protected fun JSONObject.getUri(attr: Attribute): Uri = Uri.parse(this.getString(attr.value))

    protected fun JSONObject.getContentAsArray(): JSONArray = this.getJSONArray("content")

    protected fun JSONObject.getContentAsString(): String = this.getString("content")

    protected fun JSONObject.getContentAsUri(): Uri = Uri.parse(this.getContentAsString())

    private fun List<String>.mapStringToTextStyle(): TextStyle =
        when(this.size) {
            1 -> {
                when(this[0]) {
                    "bold" -> TextStyle.BOLD
                    "italic" -> TextStyle.ITALIC
                    else -> throw UnsupportedOperationException("This style is not supported: $this[0]")
                }
            }

            2 -> {
                if(this.contains("bold") && this.contains("italic")) {
                    TextStyle.BOLD_ITALIC
                } else {
                    throw UnsupportedOperationException("Invalid styles combination")
                }
            }
            else -> throw UnsupportedOperationException("Invalid size of styles set")
        }
}