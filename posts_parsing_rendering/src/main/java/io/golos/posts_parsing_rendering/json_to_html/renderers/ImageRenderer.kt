package io.golos.posts_parsing_rendering.json_to_html.renderers

import io.golos.posts_parsing_rendering.Attribute
import io.golos.posts_parsing_rendering.json_to_html.links_repository.LinksRepository
import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.utils.tryString
import org.json.JSONObject

class ImageRenderer(
    builder: HtmlBuilder,
    private val linksRepository: LinksRepository
) : RendererBase(builder) {
    override fun render(block: JSONObject) {
        linksRepository.putLink(block)

        val url = block.getString("content")

        val attributes = getAttributes(block)

        val description = attributes?.tryString(Attribute.DESCRIPTION.value)

        builder.putBlockAnchor(getId(block)) {
            builder.putFigure(url) {
                description?.let { builder.putFigureCaption(it) }
            }
        }
    }
}