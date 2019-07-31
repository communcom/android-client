package io.golos.cyber_android.ui.common.extensions

import android.widget.EditText

fun EditText.moveCursorToTheEnd() {
    if (this.isFocused) {
        this.setSelection(this.text.toString().length)
    }
}