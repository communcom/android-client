package io.golos.cyber_android.ui.shared.formatters.percent

object PercentFormatter {
    /**
     * [value] should be in 0..1
     */
    fun format(value: Double): String = "${(value*100).toInt()}%"
}