package io.golos.utils.helpers

import android.net.Uri
import io.golos.utils.BuildConfig
import timber.log.Timber
import java.lang.Exception
import java.util.*
import java.util.regex.Pattern

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

fun String.capitalize(locale: Locale = Locale.getDefault()): String {
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

fun String.isMatch(s: String): Boolean = Pattern.compile(this).matcher(s).find()

fun Pattern.isMatch(s: String): Boolean = this.matcher(s).find()

fun CharSequence.splitToCharSequences(delimiter: String): List<CharSequence> {
    val result = mutableListOf<CharSequence>()

    if(this.isEmpty() || delimiter.isEmpty()) {
        return result
    }

    fun calculateSubRange(startIndex: Int): IntRange {
        val lastIndex = this.indexOf(delimiter, startIndex)

        if(startIndex == lastIndex) {
            return calculateSubRange(startIndex+1)
        }

        return IntRange(startIndex, lastIndex)
    }

    var subRange = calculateSubRange(0)

    while (subRange.last != -1) {
        this.subSequence(subRange.first, subRange.last).takeIf { it.isNotEmpty() }?.let { result.add(it) }

        subRange = calculateSubRange(subRange.last)
    }

    this.subSequence(subRange.first,  this.lastIndex+1).takeIf { it.isNotEmpty() }?.let { result.add(it) }

    return result
}