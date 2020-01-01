package io.golos.cyber_android.ui.shared.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.utils.ViewUtils
import kotlinx.android.synthetic.main.view_sms_code.view.*

/**
 * Custom view for sms code input
 */
class SmsCodeWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    // Called when code was changed
    private var onCodeChangedListener: ((String) -> Unit)? = null
    // Called when user press "Done" button and code has necessary length
    private var onDonePressedListener: (() -> Unit)? = null

    private val digits: List<SmsCodeEditText>

    private var freezeEvents = false

    init {
        inflate(getContext(), R.layout.view_sms_code, this)

        digits = listOf(smsCodeDigit0, smsCodeDigit1, smsCodeDigit2, smsCodeDigit3)

        digits.forEachIndexed { index, digitWidget ->
            digitWidget.setOnTextChangedListener {
                if(!freezeEvents) {
                    onTextChanged(it, index)
                }
            }

            digitWidget.setOnEmptyDelButtonClickListener {
                onDelButtonPressed(index)
            }

            digitWidget.setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus) {
                    post { digitWidget.setSelection(digitWidget.text.length) }
                }
            }
        }

        digits.last().setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && getCode().length == digits.size) {
                onDonePressedListener?.invoke()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    fun showKeyboard() {
        digits.find { it.text.isEmpty() }?.let {
            ViewUtils.showKeyboard(it)
        }
    }

    fun setOnCodeChangedListener(listener: ((String) -> Unit)?) {
        onCodeChangedListener = listener
    }

    fun setOnDonePressedListener(listener: (() -> Unit)?) {
        onDonePressedListener = listener
    }

    fun clearCode() {
        freezeEvents = true

        digits.forEach { it.setText("") }
        digits[0].requestFocus()

        onCodeChangedListener?.invoke(getCode())

        freezeEvents = false
    }

    private fun getCode() = digits
        .map { it.text.toString() }
        .reduce { acc, text -> acc + text }
        .trim()

    private fun onTextChanged(text: String, digitIndex: Int) {
        onCodeChangedListener?.invoke(getCode())

        when {
            text.isNotEmpty() && digitIndex < digits.lastIndex -> digits[digitIndex + 1].requestFocus()
            text.isEmpty() && digitIndex > 0 -> onDelButtonPressed(digitIndex)
        }
    }

    private fun onDelButtonPressed(digitIndex: Int) {
        if(digitIndex > 0) {
            digits[digitIndex - 1].requestFocus()
        }
    }
}