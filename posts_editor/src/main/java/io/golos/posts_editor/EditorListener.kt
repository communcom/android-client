package io.golos.posts_editor

import android.text.Editable
import android.widget.EditText

interface EditorListener {
    fun onTextChanged(editText: EditText, text: Editable)
}