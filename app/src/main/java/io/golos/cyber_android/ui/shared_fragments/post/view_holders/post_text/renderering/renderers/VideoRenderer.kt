package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.links_repository.LinksRepository
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import org.json.JSONObject

class VideoRenderer(
    builder: HtmlBuilder,
    private val linksRepository: LinksRepository
) : RendererBase(builder) {
    override fun render(block: JSONObject) {
        linksRepository.putLink(block)

        val url = block.getString("content")

        val attributes = getAttributes(block)

        val title = attributes?.optString("title")
        val description = attributes?.optString("description")
        val thumbnailUrl = attributes?.optString("thumbnail_url")
        val html = attributes?.optString("html")

        if(html != null) {
            builder.putString(html)
        } else {
            builder.putFigure(thumbnailUrl ?: "file:///android_asset/video_stub.webp") {
                builder.putFigureCaption(title ?: description ?: url)
            }
        }
    }
}