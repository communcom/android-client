package io.golos.posts_parsing_rendering.renderering.renderers

import io.golos.posts_parsing_rendering.renderering.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.utils.tryJSONObject
import org.json.JSONObject

abstract class RendererBase(protected val builder: HtmlBuilder) {
    abstract fun render(block: JSONObject)

    fun getAttributes(block: JSONObject): JSONObject? = block.tryJSONObject("attributes")

    fun getId(block: JSONObject): Long = block.getLong("id")
}