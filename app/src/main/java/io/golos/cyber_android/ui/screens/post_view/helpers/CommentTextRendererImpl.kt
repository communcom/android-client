package io.golos.cyber_android.ui.screens.post_view.helpers

import android.content.Context
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.annotation.ColorInt
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.extensions.getColorRes
import io.golos.cyber_android.ui.common.spans.ColorTextClickableSpan
import io.golos.cyber_android.ui.common.spans.LinkClickableSpan
import io.golos.domain.extensions.appendText
import io.golos.domain.extensions.setSpan
import io.golos.domain.use_cases.post.post_dto.*
import io.golos.domain.use_cases.post.toTypeface
import javax.inject.Inject

/**
 * Transforms text of comment to a set of strings
 */
class CommentTextRendererImpl
@Inject
constructor(
    context: Context
) : CommentTextRenderer {
    @ColorInt
    private val spansColor = context.resources.getColorRes(R.color.default_clickable_span_color)

    override fun render(post: List<Block>): List<CharSequence> =
        post
            .filterIsInstance<ParagraphBlock>()
            .map { getParagraphText(it) }

    private fun getParagraphText(block: ParagraphBlock): CharSequence {
        val builder = SpannableStringBuilder()

        block.content.forEach { blockItem ->
            when (blockItem) {
                is TextBlock -> addText(blockItem, builder)
                is TagBlock -> addTag(blockItem, builder)
                is MentionBlock -> addMention(blockItem, builder)
                is LinkBlock -> addLink(blockItem, builder)
            }
        }

        return builder
    }

    private fun addText(block: TextBlock, builder: SpannableStringBuilder) {
        val textInterval = builder.appendText(block.content)

        block.textColor?.let { builder.setSpan(ForegroundColorSpan(it), textInterval) }
        block.style?.let { builder.setSpan(StyleSpan(it.toTypeface()), textInterval) }
    }

    private fun addTag(block: TagBlock, builder: SpannableStringBuilder) {
        val textInterval = builder.appendText("#${block.content}")

        builder.setSpan(ForegroundColorSpan(spansColor), textInterval)
    }

    private fun addMention(block: MentionBlock, builder: SpannableStringBuilder) {
        val textInterval = builder.appendText("@${block.content}")

        // Click on the link
        builder.setSpan(object : ColorTextClickableSpan(block.content, spansColor) {
            override fun onClick(spanData: String) {
                // User's name
            }
        }, textInterval)
    }

    private fun addLink(block: LinkBlock, builder: SpannableStringBuilder) {
        val textInterval = builder.appendText(block.content)

        // Click on the link
        builder.setSpan(object : LinkClickableSpan(block.url, spansColor) {
            override fun onClick(spanData: Uri) {
                // onClickProcessor?.onLinkClicked(spanData)
            }
        }, textInterval)
    }
}