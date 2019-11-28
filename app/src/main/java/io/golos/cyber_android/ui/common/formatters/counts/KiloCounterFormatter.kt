package io.golos.cyber_android.ui.common.formatters.counts

import java.util.*
import kotlin.math.abs

object KiloCounterFormatter {
    fun format(count: Int): String =
        when {
            abs(count) < 1000 -> count.toString()
            abs(count) % 1000 < 51 -> "${count/1000}k"
            else -> "${String.format(Locale.US, "%.1f", count/1000.0)}k"
        }
}