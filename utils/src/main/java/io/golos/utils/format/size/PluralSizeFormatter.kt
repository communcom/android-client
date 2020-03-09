package io.golos.utils.format.size

import android.content.Context
import androidx.annotation.PluralsRes
import io.golos.utils.R
import io.golos.utils.getFormattedString

class PluralSizeFormatter
constructor(
    private val context: Context,
    @PluralsRes private val stringTemplate: Int
) : SizeFormatterBase(1000.0) {

    private companion object {
        private val sizeUnits = arrayOf(
            R.string.formatter_plurals_none,
            R.string.formatter_plurals_kilo,
            R.string.formatter_plurals_mega,
            R.string.formatter_plurals_giga)
    }

    fun format(size: Int): String =
        calculateUnitIndex(size)
            .let {
                val value = calculateValue("#.#", size, it)
                val unit = context.resources.getString(sizeUnits[it])
                val template = context.resources.getQuantityString(stringTemplate, size.toInt())
                context.resources.getFormattedString(template, value, unit)
            }
}
