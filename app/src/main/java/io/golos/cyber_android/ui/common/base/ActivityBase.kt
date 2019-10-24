package io.golos.cyber_android.ui.common.base

import android.graphics.Color
import android.view.View.*
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R


abstract class ActivityBase : AppCompatActivity() {
    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    fun setFullScreenMode() {
        val window = window
        window?.decorView?.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window?.statusBarColor = Color.TRANSPARENT
    }

    fun clearFullScreenMode() {
        val window = window
        window?.decorView?.systemUiVisibility = SYSTEM_UI_FLAG_VISIBLE
        window?.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window?.statusBarColor = ContextCompat.getColor(this, R.color.white)
    }
}