package io.golos.cyber_android.ui.common.formatters.percent

object PercentFormatter {
    /**
     * [value] should be in 0..1
     */
    fun format(value: Double): String = "${(value*100).toInt()}%"
}