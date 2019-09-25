package io.golos.posts_parsing_rendering.json_to_html.renderers

import io.golos.posts_parsing_rendering.Attribute
import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.json_to_html.links_repository.LinksRepository
import io.golos.posts_parsing_rendering.utils.tryString

import org.json.JSONObject

class VideoRenderer(
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
        val html = attributes?.tryString(Attribute.HTML.value)

        builder.putBlockAnchor(getId(block)) {
            if(html != null) {
                builder.putBlockWrapper {
                    builder.putString(html)
                    builder.putLink(url) {
                        builder.putFigureCaption(title ?: description ?: url)
                    }
                }
            } else {
                builder.putLink(url) {
                    builder.putFigure(thumbnailUrl ?: "file:///android_asset/video_stub.webp") {
                        builder.putFigureCaption(title ?: description ?: url)
                    }
                }
            }
        }
    }
}