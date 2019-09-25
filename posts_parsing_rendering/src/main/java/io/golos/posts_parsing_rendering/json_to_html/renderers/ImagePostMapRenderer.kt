package io.golos.posts_parsing_rendering.json_to_html.renderers

import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import org.json.JSONObject

class ImagePostMapRenderer(builder: HtmlBuilder) : RendererBase(builder) {
    override fun render(block: JSONObject) = builder.putPostMapBlock(block.getString("content"), getId(block))
}