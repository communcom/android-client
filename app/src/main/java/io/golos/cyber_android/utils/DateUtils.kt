package io.golos.cyber_android.utils

import android.content.Context
import io.golos.cyber_android.R
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun createTimeLabel(fromTimeStamp: Long, context: Context): String {
        val mSdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        return fromTimeStamp.minutesElapsedFromTimeStamp().let { elapsedMinutesFromPostCreation ->
            val hoursElapsed = elapsedMinutesFromPostCreation / 60
            when {
                elapsedMinutesFromPostCreation == 0 -> context.getString(R.string.now_happened)
                elapsedMinutesFromPostCreation < 60 -> "${context.resources.getQuantityString(R.plurals.minutes, elapsedMinutesFromPostCreation, elapsedMinutesFromPostCreation)}  ${context.resources.getString(R.string.ago)}"

                hoursElapsed < 24 -> "${context.resources.getQuantityString(R.plurals.hours, hoursElapsed, hoursElapsed)}  ${context.resources.getString(R.string.ago)}"
                else -> {
                    val daysAgo = Math.round(hoursElapsed.toDouble() / 24)

                    if (daysAgo <= 7)
                        "${context.resources.getQuantityString(R.plurals.days, daysAgo.toInt(), daysAgo.toInt())} ${context.resources.getString(R.string.ago)}"
                    else {
                        val timeStamp = fromTimeStamp + TimeZone.getDefault().getOffset(fromTimeStamp)
                        Calendar.getInstance(TimeZone.getDefault()).apply { timeInMillis = timeStamp }.let {
                            mSdf.format(it.time)
                        }
                    }
                }
            }
        }
    }

    fun Long.minutesElapsedFromTimeStamp(): Int {
        val currentTime = System.currentTimeMillis() - TimeZone.getDefault().getOffset(System.currentTimeMillis())
        val dif = currentTime - this
        val hour = 1000 * 60
        val hoursAgo = dif / hour
        return hoursAgo.toInt()
    }

}