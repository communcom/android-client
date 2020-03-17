package io.golos.cyber_android.ui.screens.login_sign_up_create_password.view

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.extensions.setOnDrawableEndClickListener

class PasswordFieldWidget
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : TextInputEditText(context, attrs, defStyle) {

    private var isTextVisible = true

    private var onVisibilityButtonClickListener: (() -> Unit)? = null

    init {
        updateTextVisibility()

        setOnDrawableEndClickListener {
            onVisibilityButtonClickListener?.invoke()
        }
    }

    fun setTextVisibility(isVisible: Boolean) {
        if(isVisible != isTextVisible) {
            isTextVisible = isVisible
            updateTextVisibility()
        }
    }

    fun setOnVisibilityButtonClickListener(listener: (() -> Unit)?) {
        onVisibilityButtonClickListener = listener
    }

    private fun updateTextVisibility() {
        val icon = context.getDrawable(if(isTextVisible) R.drawable.ic_open_eye_fill else R.drawable.ic_close_eye_fill)
        setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)

        inputType = if(isTextVisible) InputType.TYPE_CLASS_TEXT else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    }
}