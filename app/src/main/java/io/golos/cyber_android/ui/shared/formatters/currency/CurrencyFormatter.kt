package io.golos.cyber_android.ui.shared.formatters.currency

import java.text.DecimalFormat

object CurrencyFormatter {
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
}