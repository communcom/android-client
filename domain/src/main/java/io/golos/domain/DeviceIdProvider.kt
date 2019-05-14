package io.golos.domain

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-26.
 */
interface DeviceIdProvider {
    fun provide():String
}