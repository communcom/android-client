package io.golos.domain

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
fun <X, Y> LiveData<X>.map(mapFunction: Function<X, Y>): LiveData<Y> = Transformations.map(this, mapFunction)

fun <X> LiveData<X>.distinctUntilChanged() = Transformations.distinctUntilChanged(this)
