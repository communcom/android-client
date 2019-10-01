package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.widgets

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.UIComponent
import io.golos.domain.AppResourcesProvider
import io.golos.domain.post.post_dto.*
import io.golos.domain.post.toTypeface
import javax.inject.Inject

class ParagraphWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : TextView(context, attrs, defStyleAttr)  {

    @Inject
    internal lateinit var appResourcesProvider: AppResourcesProvider

    init {
        App.injections.get<UIComponent>().inject(this)

        setUp()
    }

    fun render(block: ParagraphBlock) = setText(block)

    private fun setUp() {
        setTextColor(Color.BLACK)

        textSize = appResourcesProvider.getDimens(R.dimen.text_size_17_sp)

        appResourcesProvider.getDimens(R.dimen.padding_post_paragraph).toInt().also {
            setPadding(it, 0, it, 0)
        }

        appResourcesProvider.getDimens(R.dimen.margin_block).toInt().also {
            val params = this.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = it
            params.bottomMargin = it
            layoutParams = params
        }
    }

    private fun setText(block: ParagraphBlock) {
        val builder = SpannableStringBuilder()

        block.content.forEach { blockItem ->
            when(blockItem) {
                is TextBlock -> addText(blockItem, builder)
                is TagBlock -> addTag(blockItem,builder)
                is MentionBlock -> addMention(blockItem,builder)
                is LinkBlock -> addLink(blockItem, builder)
            }
        }

        this.text = builder
    }

    private fun addText(block: TextBlock, builder: SpannableStringBuilder) {
        val textInterval = builder.appendText(block.content)

        block.textColor?.let { builder.setSpan(ForegroundColorSpan(it), textInterval) }
        block.style?.let { builder.setSpan(StyleSpan(it.toTypeface()), textInterval) }
    }

    private fun addTag(block: TagBlock, builder: SpannableStringBuilder) {
        val textInterval = builder.appendText("#${block.content}}")

        builder.setSpan(ForegroundColorSpan(Color.BLUE), textInterval)
    }

    private fun addMention(block: MentionBlock, builder: SpannableStringBuilder) {
        val textInterval = builder.appendText("@${block.content}}")

        builder.setSpan(ForegroundColorSpan(Color.BLUE), textInterval)
    }

    private fun addLink(block: LinkBlock, builder: SpannableStringBuilder) {
        val textInterval = builder.appendText("@${block.content}}")

        builder.setSpan(ForegroundColorSpan(Color.BLUE), textInterval)
    }

    private fun SpannableStringBuilder.setSpan(span: CharacterStyle, interval: IntRange) =
        this.setSpan(span, interval.first, interval.last, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

    private fun SpannableStringBuilder.appendText(text: String): IntRange {
        val start = this.length

        this.append(text)

        return start..this.length
    }
}
