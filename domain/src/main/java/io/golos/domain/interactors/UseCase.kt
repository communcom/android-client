package io.golos.domain.interactors

import androidx.lifecycle.LiveData

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
interface UseCase<T> {
    abstract val getAsLiveData: LiveData<T>

    fun subscribe() {}

    fun unsubscribe() {}
}