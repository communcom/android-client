package io.golos.utils.format

object RewardFormatter {
    fun formatPoints(value: Double): String = value.format("#.###")
    fun formatCommun(value: Double): String = value.format("#.###")
    fun formatUSD(value: Double): String = value.format("#.##")

    private fun Double.format(template: String) = CurrencyFormatter.getFormatterFromTemplate(template).format(this)
}