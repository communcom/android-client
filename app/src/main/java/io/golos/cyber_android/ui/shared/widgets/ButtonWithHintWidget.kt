package io.golos.cyber_android.ui.shared.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.view_button_with_hint_widget.view.*

/**
 * "Button" with some primary text, a secondary text and an icon
 */
class ButtonWithHintWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        inflate(getContext(), R.layout.view_button_with_hint_widget, this)

        attrs?.let { retrieveAttributes(it) }
    }

    private fun retrieveAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ButtonWithHintWidget)

        val icon = typedArray.getDrawable(R.styleable.ButtonWithHintWidget_button_with_hint_icon)!!
        icon.setTintList(typedArray.getColorStateList(R.styleable.ButtonWithHintWidget_button_with_hint_icon_tint))
        buttonIcon.setImageDrawable(icon)

        val scaleFactor = typedArray.getFloat(R.styleable.ButtonWithHintWidget_button_with_hint_icon_scale, 1f)
        buttonIcon.scaleX = scaleFactor
        buttonIcon.scaleY = scaleFactor

        background = typedArray.getDrawable(R.styleable.ButtonWithHintWidget_button_with_hint_background)!!

        primaryText.text = typedArray.getString(R.styleable.ButtonWithHintWidget_button_with_hint_primary_text)
        secondaryText.text = typedArray.getString(R.styleable.ButtonWithHintWidget_button_with_hint_secondary_text)

        typedArray.recycle()
    }
}
