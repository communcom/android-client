package io.golos.utils.amount

import java.text.DecimalFormat

fun Double.toServerFormat(): String =
    DecimalFormat()
        .apply {
            val symbols = decimalFormatSymbols
            symbols.decimalSeparator = '.'
            decimalFormatSymbols = symbols
        }
        .format(this)
