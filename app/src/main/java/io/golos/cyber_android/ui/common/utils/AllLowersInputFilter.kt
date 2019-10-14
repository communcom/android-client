package io.golos.cyber_android.ui.common.utils

import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import java.util.*

/**
 * This filter makes lowercase all letters that are added through edits
 * (implementation has been got from [android.text.InputFilter.AllCaps])
 */
class AllLowersInputFilter
@JvmOverloads
constructor(private val locale: Locale? = null): InputFilter {
    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? =
        source?.let { sourceText ->
            var isUpperCaseFound = false
            val length = end - start
            var i = 0

            while (i < length) {
                val character = Character.codePointAt(sourceText, i)
                if (Character.isUpperCase(character)) {
                    isUpperCaseFound = true
                    break
                }
                i += Character.charCount(character)
            }

            if (!isUpperCaseFound) {
                return null     // keep original
            }

            if(sourceText is Spanned) toLower(locale, sourceText) else toLower(locale, sourceText)
        }

    private fun toLower(locale: Locale?, s: Spanned): CharSequence {
        val spans = s.getSpans(0, s.length, Any::class.java)

        val spannableString = SpannableString(toLower(locale, s.toString()))

        spans.forEach { span ->
            spannableString.setSpan(span, s.getSpanStart(span), s.getSpanEnd(span), 0)
        }

        return spannableString
    }

    private fun toLower(locale: Locale?, s: CharSequence): CharSequence = toLower(locale, s.toString())

    private fun toLower(locale: Locale?, string: String) = locale?.let { string.toLowerCase(it) } ?: string.toLowerCase()
}