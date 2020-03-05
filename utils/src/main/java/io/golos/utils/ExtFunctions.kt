package io.golos.utils

inline fun <T, R> scope(receiver: T, block: (T) -> R): R {
    return block(receiver)
}
