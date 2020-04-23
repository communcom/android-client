package io.golos.utils.format

object RewardFormatter {
    fun format(value: Double): String = CurrencyFormatter.getFormatterFromTemplate("#.##").format(value)
}