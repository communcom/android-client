package io.golos.cyber_android.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.common.mvvm.SingleLiveData

@Suppress("detekt.UnsafeCast")
fun <T> MutableLiveData<T>.toLiveData() = this as LiveData<T>

fun <T> LiveData<T>.toSingleLiveData(): LiveData<T> {
    val result = SingleLiveData<T>()
    result.addSource(this) {
        result.value = it
    }
    return result
}