package io.golos.cyber_android.ui.shared.helper

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.keyboard.KeyboardUtils
import javax.inject.Inject
import android.content.res.Configuration
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import io.golos.cyber_android.application.App
import io.golos.domain.GlobalConstants

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
        val titleColor: Int
        val messageColor: Int

        val popupView = inflater.inflate(if (isError) R.layout.popup_toast_error else R.layout.popup_toast_information, null)
        popupView.findViewById<LinearLayout>(R.id.container).background = when {
            App.getInstance().keyValueStorage.getUIMode() == GlobalConstants.UI_MODE_DARK -> {
                ContextCompat.getDrawable(appContext, R.drawable.bcg_toast_message_dark)
            }
            App.getInstance().keyValueStorage.getUIMode() == GlobalConstants.UI_MODE_LIGHT -> {
                ContextCompat.getDrawable(appContext, R.drawable.bcg_toast_message)
            }
            appContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES -> {
                ContextCompat.getDrawable(appContext, R.drawable.bcg_toast_message_dark)
            }
            else -> {
                ContextCompat.getDrawable(appContext, R.drawable.bcg_toast_message)
            }
        }
        when {
            App.getInstance().keyValueStorage.getUIMode() == GlobalConstants.UI_MODE_DARK -> {
                titleColor = ContextCompat.getColor(appContext, R.color.white_dark_theme)
                messageColor = ContextCompat.getColor(appContext, R.color.light_gray_dark_theme)
            }
            App.getInstance().keyValueStorage.getUIMode() == GlobalConstants.UI_MODE_LIGHT -> {
                titleColor = ContextCompat.getColor(appContext, R.color.white)
                messageColor = ContextCompat.getColor(appContext, R.color.light_gray)
            }
            appContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES -> {
                titleColor = ContextCompat.getColor(appContext, R.color.white_dark_theme)
                messageColor = ContextCompat.getColor(appContext, R.color.light_gray_dark_theme)
            }
            else -> {
                titleColor = ContextCompat.getColor(appContext, R.color.white)
                messageColor = ContextCompat.getColor(appContext, R.color.light_gray)
            }
        }

        val title = popupView.findViewById<TextView>(R.id.title)
        title.setTextColor(titleColor)

        val textView = popupView.findViewById<TextView>(R.id.messageText)
        textView.setTextColor(messageColor)
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

    override fun setSoftKeyboardVisibility(someViewInWindow: View, isVisible: Boolean) =
        KeyboardUtils.setKeyboardVisibility(someViewInWindow, isVisible)
}