package io.golos.utils.format

import android.content.Context
import io.golos.utils.R
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

object CurrencyFormatter {
    private val sizeUnits = arrayOf(
        R.string.currency_kilo,
        R.string.currency_mega,
        R.string.currency_giga)


    fun format(value: Double): String =
        (if(value < 1000.0) {
            getFormatterFromTemplate("#.####")
        } else {
            val formatter = getFormatterFromTemplate("###,###.##")

            val symbols = formatter.decimalFormatSymbols

            symbols.groupingSeparator = ' '
            formatter.decimalFormatSymbols = symbols

            formatter
        }).format(value)

    fun formatShort(context: Context, value: Double): String {
        val basePower = 3.0

        return if (value < 10.0.pow(basePower)) {
            getFormatterFromTemplate("#.###").format(value)
        } else {
            val unitIndex = (log10(value.toInt().toDouble()) / 3).toInt()

            val base = 10.0.pow(basePower * unitIndex).toInt()

            val intValue = value.toInt()/base

            val valueToFormat = intValue + (value - (intValue * base))/base

            "${getFormatterFromTemplate("#.#").format(valueToFormat)}${context.resources.getString(sizeUnits[unitIndex - 1])}"
        }
    }

    fun getFormatterFromTemplate(template: String) =
        DecimalFormat(template)
            .apply {
                val symbols = decimalFormatSymbols
                symbols.decimalSeparator = '.'
                decimalFormatSymbols = symbols
            }
}