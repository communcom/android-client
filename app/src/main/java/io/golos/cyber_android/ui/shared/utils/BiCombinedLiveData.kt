package io.golos.cyber_android.ui.shared.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

/**
 * [LiveData] that combines results from 2 LiveData's. Results are combined via
 * [combine] function
 */
class BiCombinedLiveData<F, S, C>(
    firstSource: LiveData<F>,
    secondSource: LiveData<S>,
    private val combine: (data1: F?, data2: S?) -> C
) : MediatorLiveData<C>() {

    private var firstData: F? = null
    private var secondData: S? = null

    init {
        super.addSource(firstSource) {
            firstData = it
            value = combine(firstData, secondData)
        }
        super.addSource(secondSource) {
            secondData = it
            value = combine(firstData, secondData)
        }
    }

    override fun <T : Any?> addSource(source: LiveData<T>, onChanged: Observer<in T>) {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> removeSource(toRemote: LiveData<T>) {
        throw UnsupportedOperationException()
    }
}


fun <F, S, C> LiveData<F>.combinedWith(secondSource: LiveData<S>, combine: (data1: F?, data2: S?) -> C)
        = BiCombinedLiveData(this, secondSource, combine)