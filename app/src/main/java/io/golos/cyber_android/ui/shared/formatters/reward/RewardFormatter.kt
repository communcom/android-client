package io.golos.cyber_android.ui.shared.formatters.reward

import java.text.DecimalFormat

object RewardFormatter {
    fun format(value: Double): String = DecimalFormat("#.##").format(value)
}