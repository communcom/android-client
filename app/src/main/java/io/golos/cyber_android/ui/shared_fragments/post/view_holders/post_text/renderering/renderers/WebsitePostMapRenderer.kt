package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import org.json.JSONObject

class WebsitePostMapRenderer(builder: HtmlBuilder) : RendererBase(builder) {
    override fun render(block: JSONObject) =
        builder.putPostMapBlock(getAttributes(block)?.optString("thumbnail_url") ?: "file:///android_asset/website_stub.webp")
}