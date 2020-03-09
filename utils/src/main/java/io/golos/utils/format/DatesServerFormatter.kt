package io.golos.utils.format

import io.golos.utils.dates_local_now_calculator.DateConstants
import java.text.SimpleDateFormat
import java.util.*

object DatesServerFormatter {
    private const val DATE_SERVER_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'"

    fun formatToServer(value: Date): String {
        val format = SimpleDateFormat(DATE_SERVER_FORMAT, Locale.US)
        format.timeZone = TimeZone.getTimeZone(DateConstants.SERVER_TIME_ZONE)
        return format.format(value)
    }

    fun formatFromServer(value: String): Date {
        val dateFormat = SimpleDateFormat(DATE_SERVER_FORMAT, Locale.US)
        return dateFormat.parse(value)
    }
}

