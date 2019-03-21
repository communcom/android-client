package io.golos.domain

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import io.golos.domain.model.QueryResult

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
fun <X, Y> LiveData<X>.map(mapFunction: Function<X, Y>): LiveData<Y> = Transformations.map(this, mapFunction)

fun <X> LiveData<X>.distinctUntilChanged() = Transformations.distinctUntilChanged(this)


fun <Q, T> QueryResult<Q>.map(param: T) =
    when (this) {
        is QueryResult.Success<Q> -> QueryResult.Success(param)
        is QueryResult.Error<Q> -> QueryResult.Error(this.error, param)
        is QueryResult.Loading<Q> -> QueryResult.Loading(param)
    }
