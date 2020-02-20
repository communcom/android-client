package io.golos.utils.amount

import java.text.DecimalFormat

fun Double.toServerPoints(): String =
    DecimalFormat("0.000")
        .apply {
            val symbols = decimalFormatSymbols
            symbols.decimalSeparator = '.'
            decimalFormatSymbols = symbols
        }
        .format(this)

fun Double.toServerTokens(): String =
    DecimalFormat("0.0000")
        .apply {
            val symbols = decimalFormatSymbols
            symbols.decimalSeparator = '.'
            decimalFormatSymbols = symbols
        }
        .format(this)
