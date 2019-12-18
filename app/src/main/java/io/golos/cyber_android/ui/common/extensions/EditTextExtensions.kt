package io.golos.cyber_android.ui.common.extensions

import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import io.golos.cyber_android.ui.utils.TextWatcherBase

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