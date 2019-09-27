package io.golos.posts_parsing_rendering.json_to_html.renderers

import io.golos.posts_parsing_rendering.Attribute
import io.golos.posts_parsing_rendering.LinkTypeJson
import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.utils.tryString
import org.json.JSONObject
import java.lang.UnsupportedOperationException

class LinkPostMapRenderer(builder: HtmlBuilder) : RendererBase(builder) {
    override fun render(block: JSONObject) {
        val attributes = getAttributes(block) ?: throw IllegalArgumentException("Link attributes can't be empty")

        val type = attributes.getString(Attribute.TYPE.value)

        val imageUrl = when(type) {
            LinkTypeJson.IMAGE -> attributes.getString(Attribute.URL.value)
            LinkTypeJson.VIDEO -> attributes.tryString(Attribute.THUMBNAIL_URL.value) ?: "file:///android_asset/video_stub.webp"
            LinkTypeJson.WEBSITE -> attributes.tryString(Attribute.THUMBNAIL_URL.value) ?: "file:///android_asset/website_stub.webp"
            else -> throw UnsupportedOperationException("This type of link is not supported: $type")
        }

        builder.putPostMapBlock(imageUrl, getId(block))
    }
}