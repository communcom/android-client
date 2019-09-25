package io.golos.posts_parsing_rendering.json_to_html.renderers

import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import org.json.JSONObject

class MentionRenderer(
    builder: HtmlBuilder
) : RendererBase(builder) {
    override fun render(block: JSONObject) {
        val content = block.getString("content")
        builder.putLink("https://www.w3schools.com/html/$content") {
            builder.putString("@$content")
        }
    }
}