package io.golos.cyber_android

import android.content.Context
import io.golos.cyber_android.locator.ServiceLocator

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */

val Context.serviceLocator: ServiceLocator
    get() = getSystemService(App.SERVICE_LOCATOR) as ServiceLocator


