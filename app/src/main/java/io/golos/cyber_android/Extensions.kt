package io.golos.cyber_android

import android.content.Context
import androidx.annotation.IdRes
import androidx.navigation.NavController
import io.golos.cyber_android.locator.ServiceLocator

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */

val Context.serviceLocator: ServiceLocator
    get() = applicationContext.getSystemService(App.SERVICE_LOCATOR) as ServiceLocator


fun NavController.safeNavigate(@IdRes from: Int, @IdRes action: Int) {
    if (this.currentDestination?.id == from)
        this.navigate(action)
}


