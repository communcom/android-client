package io.golos.utils.dates

import java.text.SimpleDateFormat
import java.util.*

fun Date.toServerFormat(): String {
    val format = SimpleDateFormat(DATE_SERVER_FORMAT, Locale.US)
    format.timeZone = TimeZone.getTimeZone(DateConstants.SERVER_TIME_ZONE)
    return format.format(this)
}

fun String.fromServerFormat(): Date {
    val dateFormat = SimpleDateFormat(DATE_SERVER_FORMAT, Locale.US)
    return dateFormat.parse(this)
}

private const val DATE_SERVER_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'"
