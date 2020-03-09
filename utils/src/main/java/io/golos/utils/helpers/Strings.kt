package io.golos.utils.helpers

import android.net.Uri
import io.golos.utils.BuildConfig
import timber.log.Timber
import java.lang.Exception
import java.util.*

/** Contain utils for work with [String]
 *
 */
const val SPACE = " "
const val EMPTY = ""

fun String?.toAbsoluteUrl(): String? {
    return this?.let {
        return StringBuilder()
            .append(BuildConfig.BASE_URL)
            .append(this)
            .toString()
    }
}

fun String?.toUri(): Uri?{
    return try {
        Uri.parse(this)
    } catch (e: Exception){
        Timber.e(e)
        null
    }
}

fun String.capitalize(locale: Locale): String {
    if (isNotEmpty()) {
        val firstChar = this[0]
        if (firstChar.isLowerCase()) {
            return buildString {
                val titleChar = firstChar.toTitleCase()
                if (titleChar != firstChar.toUpperCase()) {
                    append(titleChar)
                } else {
                    append(this@capitalize.substring(0, 1).toUpperCase(locale))
                }
                append(this@capitalize.substring(1))
            }
        }
    }
    return this
}
