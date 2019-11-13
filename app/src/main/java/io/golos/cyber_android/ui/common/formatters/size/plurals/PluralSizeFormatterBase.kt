package io.golos.cyber_android.ui.common.formatters.size.plurals

import androidx.annotation.PluralsRes
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.formatters.size.SizeFormatter
import io.golos.cyber_android.ui.common.formatters.size.SizeFormatterBase
import io.golos.domain.AppResourcesProvider

abstract class PluralSizeFormatterBase
constructor(
    private val appResources: AppResourcesProvider,
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
                val unit = appResources.getString(sizeUnits[it])
                val template = appResources.getQuantityString(stringTemplate, size.toInt())
                appResources.getFormattedString(template, value, unit)
            }
}
