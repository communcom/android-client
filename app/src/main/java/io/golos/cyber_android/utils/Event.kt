package io.golos.cyber_android.utils

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import io.golos.domain.map

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
class Event<out T>(private val content: T?) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content and prevents its further use if content satisfies [predicate].
     */
    fun getIf(predicate: (T?.() -> Boolean)): T? {
        return if (hasBeenHandled || !predicate(content)) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}

fun <T> LiveData<T>.asEvent() = this.map(Function<T, Event<T>> { Event(this.value) } )
