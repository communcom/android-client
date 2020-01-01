package io.golos.cyber_android.ui.shared.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeUntil(owner: LifecycleOwner, stopCondition: (T) -> Boolean, observer: (T) -> Unit) {
    observe(owner, object: Observer<T> {
        override fun onChanged(value: T) {
            if (stopCondition(value))
                removeObserver(this)
            observer(value)
        }
    })
}