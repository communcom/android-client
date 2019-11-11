package io.golos.domain.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import io.golos.domain.use_cases.model.ElapsedTime
import io.golos.domain.requestmodel.QueryResult
import java.util.*

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
fun <X, Y> LiveData<X>.map(mapFunction: (X?) -> Y): LiveData<Y> = Transformations.map(this, mapFunction)

fun <X> LiveData<X>.distinctUntilChanged() = Transformations.distinctUntilChanged(this)


fun <Q, T> QueryResult<Q>.map(param: T) =
    when (this) {
        is QueryResult.Success<Q> -> QueryResult.Success(param)
        is QueryResult.Error<Q> -> QueryResult.Error(this.error, param)
        is QueryResult.Loading<Q> -> QueryResult.Loading(param)
    }


fun Date.asElapsedTime(): ElapsedTime {
    val fromTimeStamp = this.time

    return fromTimeStamp.minutesElapsedFromTimeStamp().let { elapsedMinutesFromPostCreation ->
        val hoursElapsed = elapsedMinutesFromPostCreation / 60
        when {
            elapsedMinutesFromPostCreation < 60 -> ElapsedTime(elapsedMinutesFromPostCreation, 0, 0)
            hoursElapsed < 24 -> ElapsedTime(elapsedMinutesFromPostCreation, hoursElapsed, 0)
            else -> {
                val daysAgo = Math.round(hoursElapsed.toDouble() / 24)
                ElapsedTime(elapsedMinutesFromPostCreation, hoursElapsed, daysAgo.toInt())
            }
        }
    }
}

internal fun Long.minutesElapsedFromTimeStamp(): Int {
    val currentTime = System.currentTimeMillis()
    val dif = currentTime - this
    val hour = 1000 * 60
    val hoursAgo = dif / hour
    return hoursAgo.toInt()
}