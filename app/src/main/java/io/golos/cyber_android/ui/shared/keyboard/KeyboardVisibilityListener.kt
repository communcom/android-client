package io.golos.cyber_android.ui.shared.keyboard

import androidx.fragment.app.Fragment

interface KeyboardVisibilityListener {
    fun setOnKeyboardOpenedListener(listener: ((Int) -> Unit)?)

    fun setOnKeyboardClosedListener(listener: ((Int) -> Unit)?)

    fun start(fragment: Fragment)

    fun stop(fragment: Fragment)
}