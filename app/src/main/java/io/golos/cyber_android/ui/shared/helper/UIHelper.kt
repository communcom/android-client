package io.golos.cyber_android.ui.shared.helper

import android.view.View
import androidx.annotation.StringRes

interface UIHelper {
    fun showMessage(@StringRes messageResId: Int, isError: Boolean = true)

    fun showMessage(message: String, isError: Boolean = true)

    fun hideMessage()

    fun setSoftKeyboardVisibility(someViewInWindow: View, isVisible: Boolean)
}