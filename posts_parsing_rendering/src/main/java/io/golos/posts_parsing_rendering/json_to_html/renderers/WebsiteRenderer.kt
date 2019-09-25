package io.golos.posts_parsing_rendering.json_to_html.renderers

import io.golos.posts_parsing_rendering.Attribute
import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.json_to_html.links_repository.LinksRepository
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

        val title = attributes?.tryString(Attribute.TITLE.value)
        val description = attributes?.tryString(Attribute.DESCRIPTION.value)
        val thumbnailUrl = attributes?.tryString(Attribute.THUMBNAIL_URL.value)
        val provider = attributes?.tryString(Attribute.PROVIDER_NAME.value)

        builder.putBlockAnchor(getId(block)) {
            builder.putLink(url) {
                builder.putFigure(thumbnailUrl ?: "file:///android_asset/website_stub.webp") {
                    builder.putFigureCaption(title ?: description ?: provider ?: url)
                }
            }
        }
    }
}