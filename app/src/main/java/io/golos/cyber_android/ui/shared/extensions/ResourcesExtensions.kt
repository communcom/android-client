package io.golos.cyber_android.ui.shared.extensions

import android.content.res.Resources
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import io.golos.cyber_android.R
import java.text.MessageFormat

@Suppress("DEPRECATION")
@ColorInt
fun Resources.getColorRes(@ColorRes resId: Int): Int =
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.getColor(resId, null)
    } else {
        this.getColor(resId)
    }

fun Resources.getFormattedString(resId: Int, vararg args: Any): String = MessageFormat.format(getString(resId), *args)

fun Resources.getFormattedString(string: String, vararg args: Any): String = MessageFormat.format(string, *args)

fun Resources.getLocale(): String = getString(R.string.locale)

fun Resources.getDrawableRes(@DrawableRes id: Int) = getDrawable(id, null)

/**
 * @return in pixels
 */
fun Resources.getStatusBarHeight(): Int {
    val resourceId = getIdentifier("status_bar_height", "dimen", "android")
    return getDimensionPixelSize(resourceId)
}