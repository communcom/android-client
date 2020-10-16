package io.golos.cyber_android.ui.shared.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import io.golos.cyber_android.application.App
import java.text.MessageFormat

fun Resources.getFormattedString(@StringRes resId: Int, vararg args: Any): String {
    return MessageFormat.format(getString(resId), *args)
}

fun Resources.getFormattedString(string: String, vararg args: Any): String {
    return MessageFormat.format(MessageFormat.format(string, *args))
}

fun getStyledAttribute(@AttrRes resId: Int, context: Context? = null): Int {
    val typedValue = TypedValue()
    (context ?: App.getInstance().applicationContext).theme.resolveAttribute(resId, typedValue, true)
    return typedValue.data
}
