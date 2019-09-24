package io.golos.posts_parsing_rendering.renderering.renderers

import io.golos.posts_parsing_rendering.renderering.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.utils.tryString
import org.json.JSONObject

class VideoPostMapRenderer(builder: HtmlBuilder) : RendererBase(builder) {
    override fun render(block: JSONObject) =
        builder
            .putPostMapBlock(
                getAttributes(block)?.tryString("thumbnail_url") ?: "file:///android_asset/video_stub.webp", getId(block))
}