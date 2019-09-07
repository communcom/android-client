package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers.exceptions.IncompatibleVersionsException
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers_factory.RenderersFactory
import org.json.JSONObject

class PostRenderer(
    private val factory: RenderersFactory,
    builder: HtmlBuilder
) : RendererBase(builder) {
    private val rendererVersion = 1

    override fun render(block: JSONObject) {
        val attributes = getAttributes(block) ?: throw IllegalArgumentException("Post attributes can't be empty")

        val postVersion = attributes.getLong("version")

        if(postVersion > rendererVersion) {
            throw IncompatibleVersionsException()
        }

        builder.putDoctype()
        builder.putHtml {
            builder.putStyles()

            builder.putBody {
                builder.putScript()

                val content = block.getJSONArray("content")

                for(i in 0 until content.length()) {
                    content.getJSONObject(i)
                        .also { factory.getRenderer(it).render(it) }

                }
            }
        }
    }
}