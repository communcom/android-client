package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import io.golos.cyber_android.utils.tryString
import org.json.JSONObject

class VideoPostMapRenderer(builder: HtmlBuilder) : RendererBase(builder) {
    override fun render(block: JSONObject) =
        builder
            .putPostMapBlock(
                getAttributes(block)?.tryString("thumbnail_url") ?: "file:///android_asset/video_stub.webp", getId(block))
}