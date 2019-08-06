package io.golos.cyber_android.ui.common.extensions

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController

fun NavController.safeNavigate(@IdRes from: Int, @IdRes action: Int, args: Bundle? = null) {
    if (this.currentDestination?.id == from)
        this.navigate(action, args)
}

fun <T> LiveData<T>.observeUntil(owner: LifecycleOwner, stopCondition: (T) -> Boolean, observer: (T) -> Unit) {
    observe(owner, object: Observer<T> {
        override fun onChanged(value: T) {
            if (stopCondition(value))
                removeObserver(this)
            observer(value)
        }
    })
}

fun View.setPadding(padding: Int) = this.setPadding(padding, padding, padding, padding)