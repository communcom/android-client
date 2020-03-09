package io.golos.utils.format.size

import android.content.Context
import io.golos.utils.R
import io.golos.utils.getFormattedString

/** Format size with units of measure */
class FileSizeFormatter(private val context: Context): SizeFormatterBase(1024.0) {
    private companion object {
        private val sizeUnits = arrayOf(
            R.string.formatter_files_none,
            R.string.formatter_files_kilo,
            R.string.formatter_files_mega,
            R.string.formatter_files_giga)
    }

    fun format(size: Int): String =
        calculateUnitIndex(size)
            .let {
                context.resources.getFormattedString(R.string.formatter_files_formatted, calculateValue("##0.#", size, it), sizeUnits[it])
            }
}
