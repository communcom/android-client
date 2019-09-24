package io.golos.posts_parsing_rendering.renderering.renderers

import io.golos.posts_parsing_rendering.renderering.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.renderering.links_repository.LinksRepository
import org.json.JSONObject

class LinkRenderer(
    builder: HtmlBuilder,
    private val linksRepository: LinksRepository
) : RendererBase(builder) {
    override fun render(block: JSONObject) {
        linksRepository.putLink(block)

        val attributes = getAttributes(block) ?: throw IllegalArgumentException("Link attributes can't be empty")

        val content = block.getString("content")
        val url = attributes.getString("url")

        builder.putLink(url, getId(block)) {
            builder.putString(content)
        }
    }
}