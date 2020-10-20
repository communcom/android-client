package io.golos.cyber_android.ui.shared.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


fun View.hideSoftKeyboard(delayMillis: Long = 300) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    postDelayed({
        imm.hideSoftInputFromWindow(windowToken, 0)
    }, delayMillis)
}