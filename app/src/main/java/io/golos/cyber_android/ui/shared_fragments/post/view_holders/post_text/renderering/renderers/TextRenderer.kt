package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import io.golos.cyber_android.utils.contains
import io.golos.cyber_android.utils.tryJSONArray
import io.golos.cyber_android.utils.tryString
import org.json.JSONObject

class TextRenderer(builder: HtmlBuilder): RendererBase(builder) {
    override fun render(block: JSONObject) {
        val attributes = getAttributes(block)

        val content = block.getString("content")

        val style = attributes?.tryJSONArray("style")
        val textColor = attributes?.tryString("text_color") ?: ""

        builder.putBoldIf({style.contains("bold")}) {
            builder.putItalicIf({style.contains("italic")}) {
                builder.putTextColorIf(textColor, { textColor!= "" }) {
                    builder.putString(content)
                }
            }
        }
    }
}