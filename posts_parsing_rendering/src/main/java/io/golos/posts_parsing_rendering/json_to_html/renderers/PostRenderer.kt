package io.golos.posts_parsing_rendering.json_to_html.renderers

import io.golos.domain.post.post_dto.PostFormatVersion
import io.golos.posts_parsing_rendering.Attribute
import io.golos.posts_parsing_rendering.GlobalConstants.postFormatVersion
import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.json_to_dto.IncompatibleVersionsException
import io.golos.posts_parsing_rendering.json_to_html.renderers_factory.RenderersFactory
import org.json.JSONObject

class PostRenderer(
    private val factory: RenderersFactory,
    builder: HtmlBuilder
) : RendererBase(builder) {

    override fun render(block: JSONObject) {
        val attributes = getAttributes(block) ?: throw IllegalArgumentException("Post attributes can't be empty")

        val postVersion = PostFormatVersion.parse(attributes.getString(Attribute.VERSION.value))

        if(postVersion.major > postFormatVersion.major) {
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