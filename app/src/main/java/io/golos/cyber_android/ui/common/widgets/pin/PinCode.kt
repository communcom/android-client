package io.golos.cyber_android.ui.common.widgets.pin

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import io.golos.cyber_android.R
import kotlinx.android.synthetic.main.view_pin_code_widget.view.*

class PinCode
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var currentActiveDigitIndex = 0
    private val digitWidgets: List<PinDigit>

    var isActive = false

    init {
        inflate(getContext(), R.layout.view_pin_code_widget, this)

        digitWidgets = listOf(digit1, digit2, digit3, digit4)
        reset()
    }

    /**
     * @return completed pin-code or null if the pin-code is not completed yet
     */
    fun setDigit(digit: Digit): String? {
        digitWidgets[currentActiveDigitIndex].digit = digit

        if(currentActiveDigitIndex > 0) {
            digitWidgets[currentActiveDigitIndex - 1].hideDigit()
        }

        if(currentActiveDigitIndex < digitWidgets.indices.last) {
            digitWidgets[currentActiveDigitIndex++].setDrawableState(PinDigit.DrawableState.FILL)
        } else {
            digitWidgets[currentActiveDigitIndex].setDrawableState(PinDigit.DrawableState.FILL)
        }

        return calculatePinCode()
    }

    fun reset() {
        currentActiveDigitIndex = 0
        digitWidgets.forEach {
            it.digit = null
            it.setDrawableState(PinDigit.DrawableState.EMPTY)
        }
    }

    /**
     * @return completed pin-code or null if the pin-code is not completed yet
     */
    private fun calculatePinCode(): String? =
        if(digitWidgets.any { it.digit == null }) {
            null
        } else {
            digitWidgets.joinToString("") { it.digit!!.value.toString() }
        }
}