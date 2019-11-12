package io.golos.cyber_android.utils

fun Int?.positiveValue(): Long{
    return if(this != null && this >= 0) this else 0
}