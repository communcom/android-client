package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import org.json.JSONObject

class TagRenderer(
    builder: HtmlBuilder
) : RendererBase(builder) {
    override fun render(block: JSONObject) {
        val attributes = getAttributes(block) ?: throw IllegalArgumentException("Post attributes can't be empty")

        val content = block.getString("content")
        val anchor = attributes.getString("anchor")

        builder.putLink("#$content", "https://www.w3schools.com/html/$anchor")
    }
}