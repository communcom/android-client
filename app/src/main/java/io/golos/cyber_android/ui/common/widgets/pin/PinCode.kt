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

    var isInErrorMode = false
    set(value) {
        field = value
        updateState()
    }

    var isActive = false
    set(value) {
        field = value
        updateState()
    }

    init {
        inflate(getContext(), R.layout.view_pin_code_widget, this)

        digitWidgets = listOf(digit1, digit2, digit3, digit4)

        digitWidgets.forEach {
            it.setOnClickListener { digit ->
                setActiveDigit(digit.tag.toString().toInt())
            }
        }

        updateState()
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
            setActiveDigit(++currentActiveDigitIndex)
        }

        return calculatePinCode()
    }

    fun reset() {
        currentActiveDigitIndex = 0
        digitWidgets.forEach {
            it.digit = null
        }
        updateState()
    }

    private fun updateState() {
        if(isActive) {
            digitWidgets.forEachIndexed { index, widget ->
                val state = if (index == currentActiveDigitIndex) getActiveMode() else getNormalModeMode()
                widget.setDrawableState(state)
            }
        } else {
            digitWidgets.forEach { widget ->
                widget.setDrawableState(getNormalModeMode())
            }
        }
    }

    private fun setActiveDigit(index: Int) {
        currentActiveDigitIndex = index
        updateState()
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

    private fun getActiveMode() = if(isInErrorMode) PinDigit.DrawableState.ACTIVE_ERROR else PinDigit.DrawableState.ACTIVE

    private fun getNormalModeMode() = if(isInErrorMode) PinDigit.DrawableState.NORMAL_ERROR else PinDigit.DrawableState.NORMAL
}