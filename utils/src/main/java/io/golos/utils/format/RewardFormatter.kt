package io.golos.utils.format

import java.text.DecimalFormat

object RewardFormatter {
    fun format(value: Double): String = DecimalFormat("#.##").format(value)
}