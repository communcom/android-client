package io.golos.cyber_android.ui.shared.formatters.time_estimation

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.extensions.getFormattedString
import java.text.SimpleDateFormat
import java.util.*

/**
 * Calculates estimation between now and some moment in a past
 */
class TimeEstimationFormatter(private val context: Context): DateTimeFormatter {
    // Time units in milliseconds
    private companion object {
        const val second = 1000L
        const val minute = second * 60
        const val hour = minute * 60
        const val day = hour * 24
        const val week = day * 7
        const val month = day * 30     // :) - was allowed by Vlad
    }

    override fun format(dateTime: Date): String {
        val now = Date().time
        val estimated = dateTime.time

        return when {
            estimated >= now ->
                context.resources.getString(R.string.date_time_format_just_now)  // Somewhere in a future

            now - estimated < minute ->
                context.resources.getString(R.string.date_time_format_just_now)

            now - estimated < hour ->
                context.resources.getFormattedString(R.string.date_time_format_minutes_ago, (now - estimated)/ minute)

            now - estimated < day ->
                context.resources.getFormattedString(R.string.date_time_format_hours_ago, (now - estimated)/ hour)

            now - estimated < week ->
                context.resources.getFormattedString(R.string.date_time_format_days_ago, (now - estimated)/ day)

            now - estimated < month ->
                context.resources.getFormattedString(R.string.date_time_format_weeks_ago, (now - estimated)/ week)

            else -> SimpleDateFormat("MM.dd.yyyy", Locale.getDefault()).format(dateTime)
        }
    }
}