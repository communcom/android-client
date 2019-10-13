package io.golos.data

import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executor

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */

val logger = object : Logger {
    override fun invoke(e: Exception) {
        e.printStackTrace()
    }
}

val dispatchersProvider = object : DispatchersProvider {
    override val uiDispatcher: CoroutineDispatcher = Executor { command -> command?.run() }.asCoroutineDispatcher()
    override val calculationsDispatcher: CoroutineDispatcher = Executor { command -> command?.run() }.asCoroutineDispatcher()
    override val ioDispatcher: CoroutineDispatcher = Executor { command -> command?.run() }.asCoroutineDispatcher()
}