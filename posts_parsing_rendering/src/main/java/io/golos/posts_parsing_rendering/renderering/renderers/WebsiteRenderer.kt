package io.golos.posts_parsing_rendering.renderering.renderers

import io.golos.posts_parsing_rendering.renderering.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.renderering.links_repository.LinksRepository
import io.golos.posts_parsing_rendering.utils.tryString
import org.json.JSONObject

class WebsiteRenderer(
    builder: HtmlBuilder,
    private val linksRepository: LinksRepository
) : RendererBase(builder) {
    override fun render(block: JSONObject) {
        linksRepository.putLink(block)

        val url = block.getString("content")

        val attributes = getAttributes(block)

        val title = attributes?.tryString("title")
        val description = attributes?.tryString("description")
        val thumbnailUrl = attributes?.tryString("thumbnail_url")
        val provider = attributes?.tryString("provider_name")

        builder.putBlockAnchor(getId(block)) {
            builder.putLink(url) {
                builder.putFigure(thumbnailUrl ?: "file:///android_asset/website_stub.webp") {
                    builder.putFigureCaption(title ?: description ?: provider ?: url)
                }
            }
        }
    }
}