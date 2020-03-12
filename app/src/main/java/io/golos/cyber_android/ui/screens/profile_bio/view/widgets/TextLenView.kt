package io.golos.cyber_android.ui.screens.profile_bio.view.widgets

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import io.golos.cyber_android.R
import io.golos.utils.getColorRes
import io.golos.utils.helpers.appendColorText

class TextLenView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : TextView(context, attrs, defStyleAttr) {

    private val mainTextColor = context.resources.getColorRes(R.color.white)
    private val darkTextColor = context.resources.getColorRes(R.color.grey)

    init {
        setTextColor(mainTextColor)
        gravity = Gravity.CENTER
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)

        setBackgroundResource(R.drawable.bcg_post_editor_text_len)
    }

    fun setTextLen(current: Int, total: Int) {
        val textBuilder = SpannableStringBuilder()

        textBuilder.appendColorText(current.toString(), mainTextColor)
        textBuilder.appendColorText("/$total", darkTextColor)

        text = textBuilder
    }
}