package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.links_repository.LinksRepository
import org.json.JSONObject

class WebsiteRenderer(
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
        val provider = attributes?.optString("provider_name")

        builder.putFigure(thumbnailUrl ?: "file:///android_asset/website_stub.webp") {
            builder.putFigureCaption(title ?: description ?: provider ?: url)
        }
    }
}