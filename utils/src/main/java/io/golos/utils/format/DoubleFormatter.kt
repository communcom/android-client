package io.golos.utils.format

import java.text.DecimalFormat

object DoubleFormatter {
    fun formatToServerPoints(value: Double): String =
        DecimalFormat("0.000")
            .apply {
                val symbols = decimalFormatSymbols
                symbols.decimalSeparator = '.'
                decimalFormatSymbols = symbols
            }
            .format(value)

    fun formatToServerTokens(value: Double): String =
        DecimalFormat("0.0000")
            .apply {
                val symbols = decimalFormatSymbols
                symbols.decimalSeparator = '.'
                decimalFormatSymbols = symbols
            }
            .format(value)
}