package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
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