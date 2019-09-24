package io.golos.posts_parsing_rendering.utils

import org.json.JSONArray
import org.json.JSONObject

fun JSONArray?.contains(value: String): Boolean {
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

fun JSONObject.tryString(name: String): String? = this.takeIf { it.has(name) }?.getString(name)

fun JSONObject.tryJSONObject(name: String): JSONObject? = this.optJSONObject(name)

fun JSONObject.tryJSONArray(name: String): JSONArray? = this.optJSONArray(name)