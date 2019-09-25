package io.golos.posts_parsing_rendering.json_to_html.renderers

import io.golos.posts_parsing_rendering.Attribute
import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.utils.tryString
import org.json.JSONObject

class WebsitePostMapRenderer(builder: HtmlBuilder) : RendererBase(builder) {
    override fun render(block: JSONObject) =
        builder
            .putPostMapBlock(
                getAttributes(block)?.tryString(Attribute.THUMBNAIL_URL.value) ?: "file:///android_asset/website_stub.webp", getId(block))
}