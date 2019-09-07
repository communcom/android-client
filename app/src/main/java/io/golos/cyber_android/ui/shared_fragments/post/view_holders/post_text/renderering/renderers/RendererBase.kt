package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import io.golos.cyber_android.utils.tryJSONObject
import org.json.JSONArray
import org.json.JSONObject

abstract class RendererBase(protected val builder: HtmlBuilder) {
    abstract fun render(block: JSONObject)

    fun getAttributes(block: JSONObject): JSONObject? = block.tryJSONObject("attributes")

    fun getId(block: JSONObject): Long = block.getLong("id")
}