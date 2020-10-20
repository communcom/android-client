package io.golos.utils.helpers

inline fun <T, R> scope(receiver: T, block: (T) -> R): R {
    return block(receiver)
}
