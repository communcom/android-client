package io.golos.cyber_android.ui.shared.utils

import android.content.Context
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.utils.DateUtils.ESTIMATE_COMMON_FORMAT
import io.golos.cyber_android.ui.shared.utils.DateUtils.ESTIMATE_MONTH_FORMAT
import io.golos.cyber_android.ui.shared.utils.DateUtils.MMMM_DD_YYYY_FORMAT
import io.golos.cyber_android.ui.shared.utils.DateUtils.day
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun createTimeLabel(
        fromTimeStamp: Long,
        elapsedMinutes: Int,
        elapsedHours: Int,
        elapsedDays: Int,
        context: Context
    ): String {
        val mSdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        return when {
            elapsedMinutes == 0 -> context.getString(io.golos.cyber_android.R.string.now_happened)
            elapsedMinutes < 60 -> "${context.resources.getQuantityString(
                io.golos.cyber_android.R.plurals.minutes,
                elapsedMinutes,
                elapsedMinutes
            )}  ${context.resources.getString(io.golos.cyber_android.R.string.ago)}"

            elapsedHours < 24 -> "${context.resources.getQuantityString(
                io.golos.cyber_android.R.plurals.hours,
                elapsedHours,
                elapsedHours
            )}  ${context.resources.getString(io.golos.cyber_android.R.string.ago)}"
            else -> {
                if (elapsedDays <= 7)
                    "${context.resources.getQuantityString(
                        io.golos.cyber_android.R.plurals.days,
                        elapsedDays,
                        elapsedDays
                    )} ${context.resources.getString(io.golos.cyber_android.R.string.ago)}"
                else {
                    val timeStamp = fromTimeStamp + TimeZone.getDefault().getOffset(fromTimeStamp)
                    Calendar.getInstance(TimeZone.getDefault()).apply { timeInMillis = timeStamp }.let {
                        mSdf.format(it.time)
                    }
                }
            }
        }
    }

    const val MMMM_DD_YYYY_FORMAT = "MMMM dd, yyyy"
    const val ESTIMATE_MONTH_FORMAT = "MMM dd"
    const val ESTIMATE_COMMON_FORMAT = "yyyy MMM dd"

    const val second = 1000L
    const val minute = second * 60
    const val hour = minute * 60
    const val day = hour * 24
    const val week = day * 7
    const val month = day * 30     // :) - was allowed by Vlad
    const val year = month * 12    // :) - was allowed by Vlad too
}

fun Date.toMMMM_DD_YYYY_Format(): String {
    return SimpleDateFormat(MMMM_DD_YYYY_FORMAT, Locale.US).format(this)
}

fun Date.toTimeEstimateFormat(context: Context): String{
    val now = Date().time
    val estimated = this.time
    val resources = context.resources

    return when {
        estimated >= now -> resources.getString(R.string.date_time_format_just_now)  // Somewhere in a future

        now - estimated < DateUtils.minute -> resources.getString(R.string.date_time_format_just_now)

        now - estimated < DateUtils.hour ->
            resources.getFormattedString(R.string.date_time_format_minutes_ago, (now - estimated)/ DateUtils.minute)

        now - estimated < DateUtils.day ->
            resources.getFormattedString(R.string.date_time_format_hours_ago, (now - estimated)/ DateUtils.hour)

        now - estimated < DateUtils.month ->
            resources.getFormattedString(R.string.date_time_format_days_ago, (now - estimated)/ DateUtils.day)

        now - estimated < DateUtils.year -> SimpleDateFormat(ESTIMATE_MONTH_FORMAT, Locale.getDefault()).format(this)

        else -> SimpleDateFormat(ESTIMATE_COMMON_FORMAT, Locale.getDefault()).format(this)
    }
}

fun beginToday(): Date{
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.clear(Calendar.MINUTE)
    calendar.clear(Calendar.SECOND)
    calendar.clear(Calendar.MILLISECOND)
    return calendar.time
}

fun beginYesterday(): Date{
    return Date(beginToday().time - day)
}

fun beginWeek(): Date{
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.clear(Calendar.MINUTE)
    calendar.clear(Calendar.SECOND)
    calendar.clear(Calendar.MILLISECOND)
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    return calendar.time
}

fun beginMonth(): Date{
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.clear(Calendar.MINUTE)
    calendar.clear(Calendar.SECOND)
    calendar.clear(Calendar.MILLISECOND)
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
    return calendar.time
}