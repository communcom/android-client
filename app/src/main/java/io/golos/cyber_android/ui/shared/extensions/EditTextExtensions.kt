package io.golos.cyber_android.ui.shared.extensions

import android.annotation.SuppressLint
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import io.golos.cyber_android.ui.shared.utils.TextWatcherBase

fun EditText.moveCursorToTheEnd() {
    if (this.isFocused) {
        this.setSelection(this.text.toString().length)
    }
}

fun EditText.setTextChangeListener(listener: (String) -> Unit): TextWatcher {
    val watcher = object: TextWatcherBase() {
        override fun afterTextChanged(s: Editable?) {
            super.afterTextChanged(s)
            listener(s.toString())
        }
    }

    this.addTextChangedListener(watcher)

    return watcher
}

fun EditText.setSoftDoneButtonListener(action: () -> Unit) {
    this.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            action()
        }
        false
    }
}

@SuppressLint("ClickableViewAccessibility")
fun EditText.setOnDrawableEndClickListener(listener: () -> Unit) {
    this.setOnTouchListener { v, event ->
        if(event.action == MotionEvent.ACTION_UP) {
            val endIcon = (v as EditText).compoundDrawables[2] ?: return@setOnTouchListener false

            val iconWidth = endIcon.intrinsicWidth

            val viewBounds = Rect(v.left, v.top, v.right, v.bottom)

            val touchX = event.x.toInt() + viewBounds.left
            val touchY = event.y.toInt() + viewBounds.top

            val touchableArea =Rect(viewBounds.right - iconWidth*2, viewBounds.top, viewBounds.right, viewBounds.bottom)

            return@setOnTouchListener if(touchableArea.contains(touchX, touchY)) {
                listener()
                true
            } else {
                false
            }
        }
        false
    }
}