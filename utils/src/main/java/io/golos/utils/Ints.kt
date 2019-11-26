package io.golos.utils

fun Int?.positiveValue(): Int {
    return if (this != null && this >= 0) this else 0
}