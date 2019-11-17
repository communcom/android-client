package io.golos.cyber_android.ui.common.formatters.size.plurals

import android.content.Context
import androidx.annotation.PluralsRes
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.extensions.getFormattedString
import io.golos.cyber_android.ui.common.formatters.size.SizeFormatter
import io.golos.cyber_android.ui.common.formatters.size.SizeFormatterBase

abstract class PluralSizeFormatterBase
constructor(
    private val context: Context,
    @PluralsRes private val stringTemplate: Int
) : SizeFormatterBase(1000.0),
    SizeFormatter {

    private companion object {
        private val sizeUnits = arrayOf(
            R.string.formatter_followers_none,
            R.string.formatter_followers_kilo,
            R.string.formatter_followers_mega,
            R.string.formatter_followers_giga)
    }

    override fun format(size: Long): String =
        calculateUnitIndex(size)
            .let {
                val value = calculateValue("#", size, it)
                val unit = context.resources.getString(sizeUnits[it])
                val template = context.resources.getQuantityString(stringTemplate, size.toInt())
                context.resources.getFormattedString(template, value, unit)
            }
}
