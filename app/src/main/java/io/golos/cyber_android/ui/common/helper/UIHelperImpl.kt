package io.golos.cyber_android.ui.common.helper

import android.content.Context
import android.view.View
import android.widget.Toast

class UIHelperImpl(private val appContext: Context): UIHelper {
    override fun showMessage(messageResId: Int) = Toast.makeText(appContext, messageResId, Toast.LENGTH_SHORT).show()

    override fun setSoftKeyboardVisibility(someViewInWindow: View, isVisible: Boolean) {
        someViewInWindow.post(SoftKeyboardVisibilityRunnable(appContext, someViewInWindow, isVisible))
    }
}