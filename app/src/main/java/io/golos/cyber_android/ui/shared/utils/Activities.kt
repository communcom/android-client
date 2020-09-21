package io.golos.cyber_android.ui.shared.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R


fun Activity.setStatusBarColor(@ColorRes colorId: Int){
    val window = window
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = ContextCompat.getColor(this, colorId)
}

fun Activity.setStyledStatusBarColor(@AttrRes colorId: Int){
    val window = window
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = getStyledAttribute(colorId, this)
}

fun Activity.setFullScreenMode() {
    val window = window
    window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    window?.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
    window?.statusBarColor = Color.TRANSPARENT
}

fun Activity.clearFullScreenMode() {
    val window = window
    window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    window?.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
    window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    window?.statusBarColor = getStyledAttribute(R.attr.white, this)
}