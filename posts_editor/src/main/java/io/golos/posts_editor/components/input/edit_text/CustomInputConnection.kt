package io.golos.posts_editor.components.input.edit_text

import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import com.google.android.material.textfield.TextInputEditText

class CustomInputConnection(
    private val editText: TextInputEditText,
    target: InputConnection,
    mutable: Boolean
) : InputConnectionWrapper(target, mutable) {

    override fun sendKeyEvent(event: KeyEvent): Boolean {
        if(event.action == KeyEvent.ACTION_DOWN) {
            if(event.keyCode == KeyEvent.KEYCODE_DEL || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return super.sendKeyEvent(event)
            }
        }
        return false
    }

    override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
        // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
        if (beforeLength == 1 && afterLength == 0) {
            // backspace
            val len = editText.text?.length ?: 0
            if (len == 0) {
                return sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) &&
                        sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
            }
            val selection = editText.selectionStart
            if (selection == 0)
                return false
        }
        return super.deleteSurroundingText(beforeLength, afterLength)
    }
}
