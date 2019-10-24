package io.golos.cyber_android.utils

import android.content.Context
import io.golos.cyber_android.utils.DateUtils.MMMM_DD_YYYY_FORMAT
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

}

fun Date.toMMMM_DD_YYYY_Format(): String {
    return SimpleDateFormat(MMMM_DD_YYYY_FORMAT, Locale.US).format(this)
}