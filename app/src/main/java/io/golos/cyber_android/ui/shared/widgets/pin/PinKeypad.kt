package io.golos.cyber_android.ui.shared.widgets.pin

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

    private var onDigitKeyPressListener: ((Digit) -> Unit)? = null
    private var onClearKeyPressListener: (() -> Unit)? = null

    init {
        inflate(getContext(), R.layout.view_pin_keypad_widget, this)

        button1.setOnClickListener { onDigitKeyPressListener?.invoke(Digit.DIGIT_1) }
        button2.setOnClickListener { onDigitKeyPressListener?.invoke(Digit.DIGIT_2) }
        button3.setOnClickListener { onDigitKeyPressListener?.invoke(Digit.DIGIT_3) }
        button4.setOnClickListener { onDigitKeyPressListener?.invoke(Digit.DIGIT_4) }
        button5.setOnClickListener { onDigitKeyPressListener?.invoke(Digit.DIGIT_5) }
        button6.setOnClickListener { onDigitKeyPressListener?.invoke(Digit.DIGIT_6) }
        button7.setOnClickListener { onDigitKeyPressListener?.invoke(Digit.DIGIT_7) }
        button8.setOnClickListener { onDigitKeyPressListener?.invoke(Digit.DIGIT_8) }
        button9.setOnClickListener { onDigitKeyPressListener?.invoke(Digit.DIGIT_9) }
        button0.setOnClickListener { onDigitKeyPressListener?.invoke(Digit.DIGIT_0) }
        buttonClear.setOnClickListener { onClearKeyPressListener?.invoke() }
    }

    fun setOnDigitKeyPressListener(listener: ((Digit) -> Unit)?) {
        onDigitKeyPressListener = listener
    }

    fun setOnClearKeyPressListener(listener: (() -> Unit)?) {
        onClearKeyPressListener = listener
    }
}