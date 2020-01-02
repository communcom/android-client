package io.golos.utils

import android.net.Uri
import timber.log.Timber
import java.lang.Exception

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