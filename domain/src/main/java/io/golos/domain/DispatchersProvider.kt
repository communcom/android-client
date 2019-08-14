package io.golos.domain

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
interface DispatchersProvider {
    val uiDispatcher: CoroutineDispatcher
    val calculationsDispatcher: CoroutineDispatcher
    val ioDispatcher: CoroutineDispatcher
}