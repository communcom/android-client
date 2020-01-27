package io.golos.cyber_android.ui.shared.helper

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import io.golos.cyber_android.R
import javax.inject.Inject

class UIHelperImpl
@Inject
constructor(
    private val appContext: Context
) : UIHelper {
    private var lastMessage: Toast? = null

    override fun showMessage(messageResId: Int, isError: Boolean) =
        showMessage(appContext.resources.getString(messageResId), isError)

    @SuppressLint("InflateParams")
    override fun showMessage(message: String, isError: Boolean) {
        val inflater = appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popupView = inflater.inflate(if(isError) R.layout.popup_toast_error else R.layout.popup_toast_information, null)

        val textView = popupView.findViewById<TextView>(R.id.messageText)
        textView.text = message

        Toast
            .makeText(appContext, message, Toast.LENGTH_LONG)
            .also {
                lastMessage = it
                it.view = popupView
                it.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
            }
            .show()
    }

    override fun hideMessage() {
        lastMessage?.cancel()
    }

    override fun setSoftKeyboardVisibility(someViewInWindow: View, isVisible: Boolean) {
        someViewInWindow.post(SoftKeyboardVisibilityRunnable(appContext, someViewInWindow, isVisible))
    }
}