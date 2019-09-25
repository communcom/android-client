package io.golos.posts_parsing_rendering.json_to_html.renderers

import io.golos.posts_parsing_rendering.Attribute
import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.utils.contains
import io.golos.posts_parsing_rendering.utils.tryJSONArray
import io.golos.posts_parsing_rendering.utils.tryString
import org.json.JSONObject

class TextRenderer(builder: HtmlBuilder): RendererBase(builder) {
    override fun render(block: JSONObject) {
        val attributes = getAttributes(block)

        val content = block.getString("content")

        val style = attributes?.tryJSONArray(Attribute.STYLE.value)
        val textColor = attributes?.tryString(Attribute.TEXT_COLOR.value) ?: ""

        builder.putBoldIf({style.contains("bold")}) {
            builder.putItalicIf({style.contains("italic")}) {
                builder.putTextColorIf(textColor, { textColor!= "" }) {
                    builder.putString(content)
                }
            }
        }
    }
}