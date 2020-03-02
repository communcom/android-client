package io.golos.cyber_android.ui.shared.text

import android.text.InputFilter
import android.text.Spanned

class ExcludeCharactersFilter
constructor(private vararg val excludedChars: Char, private val ignoreCase: Boolean): InputFilter {
    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        if(source == null || dest == null) {
            return null
        }

        val prediction = StringBuilder(dest)
        prediction.insert(dstart, source)

        excludedChars.forEach {
            if(prediction.contains(it,  ignoreCase)) {
                return ""
            }
        }

        return null
    }
}