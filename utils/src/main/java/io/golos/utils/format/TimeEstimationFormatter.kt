package io.golos.utils.format

import android.content.Context
import io.golos.utils.R
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Calculates estimation between now and some moment in a past
 */
object TimeEstimationFormatter {
    // Time units in milliseconds
    private const val second = 1000L
    private const val minute = second * 60
    private const val hour = minute * 60
    private const val day = hour * 24
    private const val week = day * 7
    private const val month = day * 30     // :) - was allowed by Vlad

    fun format(dateTime: Date, context: Context): String {
        val now = Date().time
        val estimated = dateTime.time

        return when {
            estimated >= now ->
                context.resources.getString(R.string.date_time_format_just_now)  // Somewhere in a future

            now - estimated < minute ->
                context.resources.getString(R.string.date_time_format_just_now)

            now - estimated < hour ->
                MessageFormat.format(context.getString(R.string.date_time_format_minutes_ago), (now - estimated)/ minute)

            now - estimated < day ->
                MessageFormat.format(context.getString(R.string.date_time_format_hours_ago), (now - estimated)/ hour)

            now - estimated < week ->
                MessageFormat.format(context.getString(R.string.date_time_format_days_ago), (now - estimated)/ day)

            now - estimated < month ->
                MessageFormat.format(context.getString(R.string.date_time_format_weeks_ago), (now - estimated)/ week)

            else -> SimpleDateFormat("MM.dd.yyyy", Locale.getDefault()).format(dateTime)
        }
    }
}