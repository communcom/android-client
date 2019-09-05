package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.LinkType
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import org.json.JSONObject
import java.lang.UnsupportedOperationException

class LinkPostMapRenderer(builder: HtmlBuilder) : RendererBase(builder) {
    override fun render(block: JSONObject) {
        val attributes = getAttributes(block) ?: throw IllegalArgumentException("Post attributes can't be empty")

        val type = attributes.getString("type")

        val imageUrl = when(type) {
            LinkType.IMAGE -> attributes.getString("url")
            LinkType.VIDEO -> attributes.optString("thumbnail_url") ?: "file:///android_asset/video_stub.webp"
            LinkType.WEBSITE -> attributes.optString("thumbnail_url") ?: "file:///android_asset/website_stub"
            else -> throw UnsupportedOperationException("This type of link is not supported: $type")
        }

        builder.putPostMapBlock(imageUrl)
    }
}