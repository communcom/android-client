package io.golos.cyber_android.ui.common.formatters.time_estimation

import java.util.*

interface DateTimeFormatter {
    fun format(dateTime: Date): String
}