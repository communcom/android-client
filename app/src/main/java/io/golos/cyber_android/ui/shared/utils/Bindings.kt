package io.golos.cyber_android.ui.shared.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Methods for simplify way binding to data
 */

fun <T> CoroutineScope.throttleLatest(
    destinationFunction: (T) -> Unit,
    intervalMs: Long = 300L
): (T) -> Unit {
    var throttleJob: Job? = null
    var latestParam: T
    return { param: T ->
        latestParam = param
        if (throttleJob?.isCompleted != false) {
            throttleJob = launch {
                delay(intervalMs)
                latestParam.let(destinationFunction)
            }
        }
    }
}

fun <T> CoroutineScope.debounce(
    destinationFunction: (T) -> Unit,
    waitMs: Long = 300L
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param: T ->
        debounceJob?.cancel()
        debounceJob = launch {
            delay(waitMs)
            destinationFunction(param)
        }
    }
}

fun <T> CoroutineScope.throttleFirst(
    destinationFunction: (T) -> Unit,
    skipMs: Long = 300L
): (T) -> Unit {
    var throttleJob: Job? = null
    return { param: T ->
        if (throttleJob?.isCompleted != false) {
            throttleJob = launch {
                destinationFunction(param)
                delay(skipMs)
            }
        }
    }
}