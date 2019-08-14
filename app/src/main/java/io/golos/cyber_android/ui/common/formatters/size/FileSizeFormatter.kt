package io.golos.cyber_android.ui.common.formatters.size

import io.golos.cyber_android.R
import io.golos.domain.AppResourcesProvider

/** Format size with units of measure */
class FileSizeFormatter(private val appResources: AppResourcesProvider): SizeFormatterBase(1024.0), SizeFormatter {
    private companion object {
        private val sizeUnits = arrayOf(
            R.string.formatter_files_none,
            R.string.formatter_files_kilo,
            R.string.formatter_files_mega,
            R.string.formatter_files_giga)
    }

    override fun format(size: Long): String =
        calculateUnitIndex(size)
            .let {
                appResources.getFormattedString(R.string.formatter_files_formatted, calculateValue("##0.#", size, it), sizeUnits[it])
            }
}
