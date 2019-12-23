package io.golos.cyber_android.ui.common.helper

import android.content.Context
import android.view.View
import android.widget.Toast
import javax.inject.Inject

class UIHelperImpl
@Inject
constructor(private val appContext: Context): UIHelper {
    private var lastMessage: Toast? = null

    override fun showMessage(messageResId: Int) =
        Toast
            .makeText(appContext, messageResId, Toast.LENGTH_SHORT)
            .also { lastMessage = it }
            .show()

    override fun showMessage(message: String) =
        Toast
            .makeText(appContext, message, Toast.LENGTH_SHORT)
            .also { lastMessage = it }
            .show()

    override fun hideMessage() {
        lastMessage?.cancel()
    }

    override fun setSoftKeyboardVisibility(someViewInWindow: View, isVisible: Boolean) {
        someViewInWindow.post(SoftKeyboardVisibilityRunnable(appContext, someViewInWindow, isVisible))
    }
}