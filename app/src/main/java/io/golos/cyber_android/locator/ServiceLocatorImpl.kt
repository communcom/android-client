package io.golos.cyber_android.locator

import android.content.Context

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
class ServiceLocatorImpl(private val appContext: Context) : ServiceLocator {

    override val getAppContext: Context
        get() = appContext

    init {

    }
}