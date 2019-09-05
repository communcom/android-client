package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers_factory.RenderersFactory
import org.json.JSONObject

class ParagraphRenderer(
    private val factory: RenderersFactory,
    builder: HtmlBuilder
) : RendererBase(builder) {
    override fun render(block: JSONObject) {
        builder.putParagraph {
            block.getJSONArray("content")
                .also { content ->
                    for(i in 0 until content.length()) {
                        content.getJSONObject(i)
                            .also { factory.getRenderer(it).render(it) }

                    }
                }
        }
    }
}