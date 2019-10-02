package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.widgets

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.UIComponent
import io.golos.cyber_android.ui.common.extensions.openLinkExternal
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.widgets.spans.LinkClickableSpan
import io.golos.domain.AppResourcesProvider
import io.golos.domain.post.post_dto.*
import io.golos.domain.post.toTypeface
import io.golos.posts_editor.utilities.post.spans.appendText
import io.golos.posts_editor.utilities.post.spans.setSpan
import javax.inject.Inject


class ParagraphWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : TextView(context, attrs, defStyleAttr),
    PostBlockWidget<ParagraphBlock> {

    @ColorInt
    private val spansColor = Color.BLUE

    @Inject
    internal lateinit var appResourcesProvider: AppResourcesProvider

    init {
        App.injections.get<UIComponent>().inject(this)
    }

    override fun render(block: ParagraphBlock) = setText(block)

    override fun cancel() {
        // do nothing
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setUp()
    }

    private fun setUp() {
        setTextColor(Color.BLACK)

        setTextSize(TypedValue.COMPLEX_UNIT_PX, appResourcesProvider.getDimens(R.dimen.text_size_post_normal))

        appResourcesProvider.getDimens(R.dimen.padding_post_paragraph).toInt().also {
            setPadding(it, 0, it, 0)
        }

        appResourcesProvider.getDimens(R.dimen.margin_block).toInt().also {
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
        val textInterval = builder.appendText("#${block.content}")

        builder.setSpan(ForegroundColorSpan(spansColor), textInterval)
    }

    private fun addMention(block: MentionBlock, builder: SpannableStringBuilder) {
        val textInterval = builder.appendText("@${block.content}")

        builder.setSpan(ForegroundColorSpan(spansColor), textInterval)
    }

    private fun addLink(block: LinkBlock, builder: SpannableStringBuilder) {
        val textInterval = builder.appendText(block.content)

        builder.setSpan(ForegroundColorSpan(spansColor), textInterval)

        // Open link in browser
        builder.setSpan(object: LinkClickableSpan(block.url, spansColor) {
            override fun onClick(spanData: Uri) = this@ParagraphWidget.openLinkExternal(spanData)
        }, textInterval)
    }
}
