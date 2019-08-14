package io.golos.cyber_android.ui.common.formatters.size

import io.golos.cyber_android.R
import io.golos.domain.AppResourcesProvider
import javax.inject.Inject

/** Format size with units of measure */
class FollowersSizeFormatter
@Inject
constructor(private val appResources: AppResourcesProvider): SizeFormatterBase(1000.0), SizeFormatter {
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
                val template = appResources.getQuantityString(R.plurals.formatter_followers_formatted, size.toInt())
                appResources.getFormattedString(template, value, unit)
            }
}
