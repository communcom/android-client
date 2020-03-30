package io.golos.posts_editor

import android.graphics.Bitmap
import android.text.Editable
import android.view.View
import android.widget.EditText

interface EditorListener {
    fun onTextChanged(editText: EditText, text: Editable)
}