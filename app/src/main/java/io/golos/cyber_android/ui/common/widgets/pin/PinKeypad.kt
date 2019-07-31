package io.golos.cyber_android.ui.common.widgets.pin

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.view_pin_keypad_widget.view.*

class PinKeypad
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onKeyPressListener: ((Digit) -> Unit)? = null

    init {
        inflate(getContext(), R.layout.view_pin_keypad_widget, this)

        button1.setOnClickListener { onKeyPressListener?.invoke(Digit.DIGIT_1) }
        button2.setOnClickListener { onKeyPressListener?.invoke(Digit.DIGIT_2) }
        button3.setOnClickListener { onKeyPressListener?.invoke(Digit.DIGIT_3) }
        button4.setOnClickListener { onKeyPressListener?.invoke(Digit.DIGIT_4) }
        button5.setOnClickListener { onKeyPressListener?.invoke(Digit.DIGIT_5) }
        button6.setOnClickListener { onKeyPressListener?.invoke(Digit.DIGIT_6) }
        button7.setOnClickListener { onKeyPressListener?.invoke(Digit.DIGIT_7) }
        button8.setOnClickListener { onKeyPressListener?.invoke(Digit.DIGIT_8) }
        button9.setOnClickListener { onKeyPressListener?.invoke(Digit.DIGIT_9) }
        button0.setOnClickListener { onKeyPressListener?.invoke(Digit.DIGIT_0) }
    }

    fun setOnKeyPressListener(listener: ((Digit) -> Unit)?) {
        onKeyPressListener = listener
    }
}