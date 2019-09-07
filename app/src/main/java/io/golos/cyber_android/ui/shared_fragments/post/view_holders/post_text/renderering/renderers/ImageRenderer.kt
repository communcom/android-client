package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.links_repository.LinksRepository
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import io.golos.cyber_android.utils.tryString
import org.json.JSONObject

class ImageRenderer(
    builder: HtmlBuilder,
    private val linksRepository: LinksRepository
) : RendererBase(builder) {
    override fun render(block: JSONObject) {
        linksRepository.putLink(block)

        val url = block.getString("content")

        val attributes = getAttributes(block)

        val description = attributes?.tryString("description")

        builder.putBlockAnchor(getId(block)) {
            builder.putFigure(url) {
                description?.let { builder.putFigureCaption(it) }
            }
        }
    }
}