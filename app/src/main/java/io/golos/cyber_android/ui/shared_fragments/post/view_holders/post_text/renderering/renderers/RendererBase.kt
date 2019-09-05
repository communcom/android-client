package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import org.json.JSONArray
import org.json.JSONObject

abstract class RendererBase(protected val builder: HtmlBuilder) {
    abstract fun render(block: JSONObject)

    fun getAttributes(block: JSONObject): JSONObject? = block.optJSONObject("attributes")

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
}