package io.golos.cyber_android.ui.common.formatters.time_estimation

import io.golos.cyber_android.R
import io.golos.domain.AppResourcesProvider
import java.text.SimpleDateFormat
import java.util.*

/**
 * Calculates estimation between now and some moment in a past
 */
class TimeEstimationFormatter(private val appResourcesProvider: AppResourcesProvider): DateTimeFormatter {
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
                appResourcesProvider.getString(R.string.date_time_format_just_now)  // Somewhere in a future

            now - estimated < minute ->
                appResourcesProvider.getString(R.string.date_time_format_just_now)

            now - estimated < hour ->
                appResourcesProvider.getFormattedString(R.string.date_time_format_minutes_ago, (now - estimated)/ minute)

            now - estimated < day ->
                appResourcesProvider.getFormattedString(R.string.date_time_format_hours_ago, (now - estimated)/ hour)

            now - estimated < week ->
                appResourcesProvider.getFormattedString(R.string.date_time_format_days_ago, (now - estimated)/ day)

            now - estimated < month ->
                appResourcesProvider.getFormattedString(R.string.date_time_format_weeks_ago, (now - estimated)/ week)

            else -> SimpleDateFormat("MM.dd.yyyy", Locale.getDefault()).format(dateTime)
        }
    }
}

/*
Отображает время создания поста в следующих форматах:
+До минуты с момента публикации - “just now”,
+до часа с момента публикации - “N minutes ago”,
+до 24 часов с момента публикации - “N hours ago”,
+до 7 дней с момента публикации - “N days ago”,
+до месяца с момента публикации - “N weeks ago”,
больше месяца с момента публикации - “mm.dd.yyyy”
*/

//<string name="date_time_format_just_now">just now</string>
//<string name="date_time_format_minutes_ago">{0} minutes ago</string>
//<string name="date_time_format_hours_ago">{0} hours ago</string>
//<string name="date_time_format_day_ago">{0} days ago</string>
//<string name="date_time_format_weeks_ago">{0} weeks ago</string>
