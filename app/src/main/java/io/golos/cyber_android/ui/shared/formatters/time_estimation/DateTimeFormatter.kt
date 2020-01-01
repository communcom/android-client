package io.golos.cyber_android.ui.shared.formatters.time_estimation

import java.util.*

interface DateTimeFormatter {
    fun format(dateTime: Date): String
}