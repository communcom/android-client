package io.golos.cyber_android.ui.shared.utils

import android.content.res.Resources
import androidx.annotation.StringRes
import java.text.MessageFormat

fun Resources.getFormattedString(@StringRes resId: Int, vararg args: Any): String {
    return MessageFormat.format(getString(resId), *args)
}

fun Resources.getFormattedString(string: String, vararg args: Any): String {
    return MessageFormat.format(MessageFormat.format(string, *args))
}