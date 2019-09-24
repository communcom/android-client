package io.golos.posts_parsing_rendering.renderering.renderers

import io.golos.posts_parsing_rendering.renderering.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.renderering.links_repository.LinksRepository
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

        val title = attributes?.tryString("title")
        val description = attributes?.tryString("description")
        val thumbnailUrl = attributes?.tryString("thumbnail_url")
        val html = attributes?.tryString("html")

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