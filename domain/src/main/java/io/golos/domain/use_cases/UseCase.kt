package io.golos.domain.use_cases

import androidx.lifecycle.LiveData

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
interface UseCase<T> {
    val getAsLiveData: LiveData<T>

    fun subscribe() {}

    fun unsubscribe() {}
}