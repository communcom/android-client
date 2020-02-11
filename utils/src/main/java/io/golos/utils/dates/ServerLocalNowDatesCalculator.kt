package io.golos.utils.dates

import java.util.*

/**
 * Calculations between server date (in UTC) & local now
 */
class ServerLocalNowDatesCalculator {
    private val localCalendar = Calendar.getInstance()      // Start value - local now
    val localNowBase = calculateDayBase(localCalendar)

    private val serverCalendar = Calendar.getInstance(TimeZone.getTimeZone(DateConstants.SERVER_TIME_ZONE))

    /**
     * [date] server date
     */
    fun calculateBase(date: Date): DateCommonBase =
        serverCalendar
            .apply { this.time = date }
            .let { calculateDayBase(it) }

    /** We lead all dates to a common base (without leap years) */
    private fun calculateDayBase(calendar: Calendar): DateCommonBase =
        DateCommonBase(calendar.get(Calendar.YEAR) * 366 + calendar.get(Calendar.DAY_OF_YEAR))
}