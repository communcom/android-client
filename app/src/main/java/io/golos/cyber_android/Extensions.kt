package io.golos.cyber_android

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.locator.ServiceLocator

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */

val Context.serviceLocator: ServiceLocator
    get() = applicationContext.getSystemService(App.SERVICE_LOCATOR) as ServiceLocator


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