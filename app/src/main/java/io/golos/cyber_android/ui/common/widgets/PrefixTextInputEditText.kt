package io.golos.cyber_android.ui.common.widgets

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

/**
 * Describe [TextInputEditText] with not editable prefix text
 */
class PrefixTextInputEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyle: Int = android.R.attr.editTextStyle) : TextInputEditText(
    context,
    attrs,
    defStyle) {

    /**
     * prefix in text
     */
    var prefix: String? = null

    override fun onSelectionChanged(startIndex: Int, endIndex: Int) {

        if (startIndex != endIndex) {
            setSelection(startIndex, startIndex)
            return
        }

        if (text != null && prefix != null) {
            val prefixLength = prefix!!.length
            if (text!!.length >= prefixLength && text!!.contains(prefix!!)) {
                if (startIndex < prefixLength) {
                    setSelection(prefixLength, prefixLength)
                } else {
                    super.onSelectionChanged(startIndex, endIndex)
                }
            } else {
                super.onSelectionChanged(startIndex, endIndex)
            }
        } else {
            super.onSelectionChanged(startIndex, endIndex)
        }
    }
}