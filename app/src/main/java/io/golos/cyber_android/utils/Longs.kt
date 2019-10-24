package io.golos.cyber_android.utils

fun Long.toPluralInt(): Int{
    return if (this > 10) 10 else this.toInt()
}