package io.golos.cyber_android.ui.shared.formatters.currency

import android.content.Context
import io.golos.cyber_android.R
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
            DecimalFormat("#.####")
        } else {
            val formatter = DecimalFormat("###,###.##")

            val symbols = formatter.decimalFormatSymbols

            symbols.groupingSeparator = ' '
            formatter.decimalFormatSymbols = symbols

            formatter
        }).format(value)

    fun formatShort(context: Context, value: Double): String {
        val basePower = 3.0

        return if (value < 10.0.pow(basePower)) {
            DecimalFormat("#.##").format(value)
        } else {
            val unitIndex = (log10(value.toInt().toDouble()) / 3).toInt()

            val base = 10.0.pow(basePower * unitIndex).toInt()

            val intValue = value.toInt()/base

            val valueToFormat = intValue + (value - (intValue * base))/base

            "${DecimalFormat("#.#").format(valueToFormat)}${context.resources.getString(sizeUnits[unitIndex - 1])}"
        }
    }
}