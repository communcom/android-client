package io.golos.cyber_android.ui.shared.keyboard

import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import javax.inject.Inject

class KeyboardVisibilityListenerImpl
@Inject
constructor() : KeyboardVisibilityListener {
    private var globalListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    private var onKeyboardOpenedListener: ((Int) -> Unit)? = null
    private var onKeyboardClosedListener: ((Int) -> Unit)? = null

    override fun setOnKeyboardOpenedListener(listener: ((Int) -> Unit)?) {
        onKeyboardOpenedListener = listener
    }

    override fun setOnKeyboardClosedListener(listener: ((Int) -> Unit)?) {
        onKeyboardClosedListener = listener
    }

    override fun start(fragment: Fragment) {
        stop(fragment)

        val activityRootView = getActivityRootView(fragment)

        globalListener = object: ViewTreeObserver.OnGlobalLayoutListener {
            private var isKeyboardOpened = false
            private var startHeightDiff = -1
            private var keyboardHeight = -1

            override fun onGlobalLayout() {
                val screenView = activityRootView.rootView

                val heightDiff = screenView.height - activityRootView.height
                if(startHeightDiff == -1) {
                    startHeightDiff = heightDiff
                } else {
                    if(keyboardHeight == -1) {
                        keyboardHeight = heightDiff - startHeightDiff
                    }
                }
                if (activityRootView.height.toFloat() / screenView.height < 0.6) {
                    if (!isKeyboardOpened) {
                        onKeyboardOpenedListener?.invoke(keyboardHeight)
                        isKeyboardOpened = true
                    }
                } else {
                    if (isKeyboardOpened) {
                        onKeyboardClosedListener?.invoke(keyboardHeight)
                        isKeyboardOpened = false
                    }
                }
            }
        }

        activityRootView.viewTreeObserver.addOnGlobalLayoutListener(globalListener)
    }

    override fun stop(fragment: Fragment) {
        globalListener?.let { getActivityRootView(fragment).viewTreeObserver.removeOnGlobalLayoutListener(it) }
        globalListener = null
    }

    private fun getActivityRootView(fragment: Fragment) =
        fragment.activity!!.window.decorView.findViewById<View>(android.R.id.content)
}