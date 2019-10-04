package io.golos.cyber_android.ui.common.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.golos.cyber_android.views.utils.TextWatcherBase

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