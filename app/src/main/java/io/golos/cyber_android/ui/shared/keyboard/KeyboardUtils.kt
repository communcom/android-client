package io.golos.cyber_android.ui.shared.keyboard

import android.view.View
import io.golos.cyber_android.ui.shared.extensions.parentActivity

object KeyboardUtils {
    fun showKeyboard(someViewInWindow: View) = setKeyboardVisibility(someViewInWindow, true)

    fun hideKeyboard(someViewInWindow: View) = setKeyboardVisibility(someViewInWindow, false)

    /**
     * Note: it'll work only for android:windowSoftInputMode="adjustResize"
     */
    fun isKeyboardVisible(someViewInWindow: View): Boolean {
        val activity = someViewInWindow.parentActivity
        val activityRootView = activity!!.window.decorView.findViewById<View>(android.R.id.content)
        val screenView = activityRootView.rootView

        return activityRootView.height.toFloat() / screenView.height < 0.6
    }

    fun setKeyboardVisibility(someViewInWindow: View, isVisible: Boolean) {
        if(someViewInWindow.isFocusable) {
            someViewInWindow.post(
                SoftKeyboardVisibilityRunnable(someViewInWindow.context, someViewInWindow, isVisible)
            )
        }
    }
}