package io.golos.cyber_android.widgets

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.widget.EditText
import io.golos.cyber_android.R

interface SmsCodeEditTextInnerState {
    val isTextEmpty: Boolean
}

class SmsCodeEditText
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle
) : EditText(context, attrs, defStyleAttr),
    SmsCodeEditTextInnerState {

    /**
     * DEL button was clicked on empty cell
     */
    private var onEmptyDelButtonClickListener: (() -> Unit)? = null

    private var onTextChangedListener: ((String) -> Unit)? = null

    /**
     * Detects when DEL key has been pressed by user for APIs <=7
     */
    private class InputConnectionOldAPIs(
        private val parent: SmsCodeEditTextInnerState,
        target: InputConnection,
        private val onDelPressed: () -> Unit
    ) : InputConnectionWrapper(target, true) {

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            if(parent.isTextEmpty) {
                onDelPressed()
            }
            return super.deleteSurroundingText(beforeLength, afterLength)
        }
    }

    /**
     * Detects when DEL key has been pressed by user for APIs > 7
     */
    private class InputConnectionNewAPIs(
        private val parent: SmsCodeEditTextInnerState,
        target: InputConnection,
        private val onDelPressed: () -> Unit
    ) : InputConnectionWrapper(target, true) {

        override fun sendKeyEvent(event: KeyEvent?): Boolean {
            if(event!!.action == KeyEvent.ACTION_UP && event.keyCode == KeyEvent.KEYCODE_DEL && parent.isTextEmpty) {
                onDelPressed()
            }
            return super.sendKeyEvent(event)
        }
    }

    init {
        val maxLen = 2

        filters = arrayOf(InputFilter.LengthFilter(maxLen))

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(editable: Editable?) {
                if(editable!!.length == maxLen) {
                    editable.replace(0, maxLen, editable[maxLen-1].toString())
                }
                onTextChangedListener?.invoke(editable.toString())
            }
        }

        this.addTextChangedListener(watcher)
    }

    override val isTextEmpty: Boolean
        get() = text.toString().isEmpty()

    fun setOnEmptyDelButtonClickListener(listener: (() -> Unit)?) {
        onEmptyDelButtonClickListener = listener
    }

    fun setOnTextChangedListener(listener: ((String) -> Unit)?) {
        onTextChangedListener = listener
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        val target = super.onCreateInputConnection(outAttrs)

        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            InputConnectionNewAPIs(this, target) { onEmptyDelButtonClickListener?.invoke() }
        } else {
            InputConnectionOldAPIs(this, target) { onEmptyDelButtonClickListener?.invoke() }
        }
    }
}
