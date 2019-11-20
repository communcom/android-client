package io.golos.utils

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