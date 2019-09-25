package io.golos.posts_parsing_rendering.json_to_html.renderers

import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.json_to_html.renderers_factory.RenderersFactory
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