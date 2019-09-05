package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import org.json.JSONObject

class TextRenderer(builder: HtmlBuilder): RendererBase(builder) {
    override fun render(block: JSONObject) {
        val attributes = getAttributes(block)

        val content = block.getString("content")

        val style = attributes?.optJSONArray("style")
        val textColor = attributes?.optString("text_color") ?: ""

        builder.putBoldIf({style.contains("bold")}) {
            builder.putItalicIf({style.contains("italic")}) {
                builder.putTextColorIf(textColor, { textColor!= "" }) {
                    builder.putString(content)
                }
            }
        }
    }
}