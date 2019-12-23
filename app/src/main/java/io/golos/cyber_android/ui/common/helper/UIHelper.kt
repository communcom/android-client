package io.golos.cyber_android.ui.common.helper

import android.view.View
import androidx.annotation.StringRes

interface UIHelper {
    fun showMessage(@StringRes messageResId: Int)

    fun showMessage(message: String)

    fun hideMessage()

    fun setSoftKeyboardVisibility(someViewInWindow: View, isVisible: Boolean)
}