package io.golos.cyber_android.ui.shared.widgets.post_comments

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.spans.ColorTextClickableSpan
import io.golos.cyber_android.ui.shared.spans.LinkClickableSpan
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.domain.extensions.appendText
import io.golos.domain.extensions.setSpan
import io.golos.domain.use_cases.post.post_dto.*
import io.golos.domain.use_cases.post.toTypeface

class ParagraphWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : TextView(context, attrs, defStyleAttr),
    BlockWidget<ParagraphBlock, ParagraphWidgetListener> {

    private var onClickProcessor: ParagraphWidgetListener? = null
    private var isSeeMoreEnabled: Boolean = false
    private var contentId: ContentId? = null

    @ColorInt
    private val spansColor: Int = ContextCompat.getColor(context, R.color.default_clickable_span_color)

    override fun setOnClickProcessor(processor: ParagraphWidgetListener?) {
        onClickProcessor = processor
    }

    override fun render(block: ParagraphBlock) = setText(block)

    override fun release() = setOnClickProcessor(null)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setUp()
    }

    fun setSeeMoreEnabled(isEnabled: Boolean) {
        isSeeMoreEnabled = isEnabled
    }

    fun setContentId(postContentId: ContentId) {
        contentId = postContentId
    }

    private fun setUp() {
        setTextColor(Color.BLACK)

        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.text_size_post_normal))

        context.resources.getDimension(R.dimen.post_content_border_horizontal).toInt().also {
            setPadding(it, 0, it, 0)
        }

        context.resources.getDimension(R.dimen.margin_block_6dp).toInt().also {
            val params = this.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = it
            params.bottomMargin = it
            layoutParams = params
        }

        movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setText(block: ParagraphBlock) {
        val builder = SpannableStringBuilder()

        block.content.forEach { blockItem ->
            when (blockItem) {
                is TextBlock -> addText(blockItem, builder)
                is TagBlock -> addTag(blockItem, builder)
                is MentionBlock -> addMention(blockItem, builder)
                is LinkBlock -> addLink(blockItem, builder)
                is SpanableBlock -> addSpannable(blockItem, builder)
            }
        }

        if (builder.length > 600 && isSeeMoreEnabled) {
            val withSeeMore = SpannableStringBuilder(builder.substring(0, 400))
            val seeMoreSpannable = SpannableStringBuilder(context.getString(R.string.see_more))
            val seeMoreClick = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    contentId?.let { id ->
                        onClickProcessor?.onSeeMoreClicked(id)
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                }
            }
            seeMoreSpannable.setSpan(
                seeMoreClick,
                0,
                seeMoreSpannable.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            seeMoreSpannable.setSpan(
                ForegroundColorSpan(spansColor),
                0,
                seeMoreSpannable.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.clear()
            builder.append(SpannableStringBuilder(TextUtils.concat(withSeeMore, "... ", seeMoreSpannable)))
        }

        this.text = builder
    }

    private fun addSpannable(block: SpanableBlock, builder: SpannableStringBuilder) {
        builder.append(block.content)
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
                onClickProcessor?.onUserClicked(spanData)           // User's name
            }
        }, textInterval)
    }

    private fun addLink(block: LinkBlock, builder: SpannableStringBuilder) {
        val textInterval = builder.appendText(block.content)

        // Click on the link
        builder.setSpan(object : LinkClickableSpan(block.url, spansColor) {
            override fun onClick(spanData: Uri) {
                onClickProcessor?.onLinkClicked(spanData)
            }
        }, textInterval)
    }
}
