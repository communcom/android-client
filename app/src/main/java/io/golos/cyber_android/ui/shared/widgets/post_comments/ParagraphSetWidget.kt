package io.golos.cyber_android.ui.shared.widgets.post_comments

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
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
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.shared.spans.ColorTextClickableSpan
import io.golos.cyber_android.ui.shared.spans.StyledTextClickableSpan
import io.golos.cyber_android.ui.shared.utils.adjustSpannableClicks
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.posts_parsing_rendering.post_metadata.TextStyle
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.*
import io.golos.domain.posts_parsing_rendering.post_metadata.toTypeface
import io.golos.utils.helpers.appendSpannable
import io.golos.utils.helpers.appendText
import io.golos.utils.helpers.setSpan

@SuppressLint("AppCompatCustomView")
class ParagraphSetWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : TextView(context, attrs, defStyleAttr),
    BlockWidget<ParagraphSet, ParagraphWidgetListener> {

    private var onClickProcessor: ParagraphWidgetListener? = null
    private var isSeeMoreEnabled: Boolean = false
    private var contentId: ContentIdDomain? = null
    private var topPadding = context.resources.getDimension(R.dimen.content_block_default_margin).toInt()
    private var bottomPadding = context.resources.getDimension(R.dimen.content_block_default_margin).toInt()
    private var startPadding = context.resources.getDimension(R.dimen.post_content_border_horizontal).toInt()
    private var endPadding = context.resources.getDimension(R.dimen.post_content_border_horizontal).toInt()

    private lateinit var paragraphSet: ParagraphSet

    @ColorInt
    private val spansColor: Int = ContextCompat.getColor(context, R.color.default_clickable_span_color)

    override fun setOnClickProcessor(processor: ParagraphWidgetListener?) {
        onClickProcessor = processor
    }

    override fun render(block: ParagraphSet) {
        this.paragraphSet = block
        setText(block)
    }

    override fun release() = setOnClickProcessor(null)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setUp()
    }

    fun setSeeMoreEnabled(isEnabled: Boolean) {
        isSeeMoreEnabled = isEnabled
    }

    fun setContentId(postContentId: ContentIdDomain) {
        contentId = postContentId
    }

    private fun setUp() {
        setTextColor(when {
            App.getInstance().keyValueStorage.getUIMode() == GlobalConstants.UI_MODE_DARK -> ContextCompat.getColor(context,R.color.black_dark_theme)
            App.getInstance().keyValueStorage.getUIMode() == GlobalConstants.UI_MODE_LIGHT -> ContextCompat.getColor(context,R.color.black)
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES -> ContextCompat.getColor(context,R.color.black_dark_theme)
            else -> ContextCompat.getColor(context,R.color.black)
        }
        )
        val spacing = context.resources.getDimension(R.dimen.text_size_post_spacing)
        setLineSpacing(spacing, 0f)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.text_size_post_normal))
        setPadding(startPadding, topPadding, endPadding, bottomPadding)
        adjustSpannableClicks()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        val params = this.layoutParams as? ViewGroup.MarginLayoutParams
        topPadding = top
        bottomPadding = bottom
        startPadding = left
        endPadding = right
        params?.let {
            super.setPadding(left, top, right, bottom)
        }
    }

    private fun setText(block: ParagraphSet) {
        val builder = SpannableStringBuilder()

        block.paragraphs.forEachIndexed { paragraphSetIndex, paragraph ->
            paragraph.content.forEachIndexed { paragraphIndex, blockItem ->
                when (blockItem) {
                    is TextBlock -> addText(paragraphIndex, blockItem, builder)
                    is TagBlock -> addTag(blockItem, builder)
                    is MentionBlock -> addMention(blockItem, builder)
                    is LinkBlock -> addLink(blockItem, builder)
                    is SpanableBlock -> addSpannable(blockItem, builder)
                }
            }

            if(paragraphSetIndex < block.paragraphs.indices.last) {
                builder.appendln()
                builder.appendln()
            }
        }

        this.text = addMore(builder)
    }

    private fun addMore(builder: SpannableStringBuilder): CharSequence {
        if (builder.length > 600 && isSeeMoreEnabled) {
            val withSeeMore = builder.subSequence(0, 400)
            val seeMoreSpannable = SpannableStringBuilder(context.getString(R.string.see_more))
            val seeMoreClick = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    contentId?.let { id ->
                        if(onClickProcessor?.onSeeMoreClicked(id) == false) {
                            isSeeMoreEnabled = false
                            setText(paragraphSet)
                        }
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

            val result = SpannableStringBuilder(withSeeMore)
            result.append("...")
            result.append(seeMoreSpannable)
            return result
        }
        return builder
    }

    private fun addSpannable(block: SpanableBlock, builder: SpannableStringBuilder) {
        builder.appendSpannable(block.content)
    }

    private fun addText(index: Int, block: TextBlock, builder: SpannableStringBuilder) {
        // note[AS] It's a dirty hack - we suppose it's an author name
        if(index == 0 && !block.content.trim().contains(" ") && block.style == TextStyle.BOLD) {
            val textInterval = builder.appendText(block.content)

            builder.setSpan(object : StyledTextClickableSpan(block.content, Typeface.DEFAULT_BOLD) {
                override fun onClick(spanData: String) {
                    onClickProcessor?.onUserClicked(spanData.trim())           // User's name
                }
            }, textInterval)
        } else {
            val textInterval = builder.appendText(block.content)

            block.textColor?.let { builder.setSpan(ForegroundColorSpan(it), textInterval) }
            block.style?.let { builder.setSpan(StyleSpan(it.toTypeface()), textInterval) }
        }
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

        builder.setSpan(object : ColorTextClickableSpan(block.content, spansColor) {
            override fun onClick(spanData: String) {
                onClickProcessor?.onLinkClicked(Uri.parse(spanData))
            }
        }, textInterval)
    }
}