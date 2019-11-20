package io.golos.utils

fun Long.toPluralInt(): Int{
    return if (this > 10) 10 else this.toInt()
}

fun Long?.positiveValue(): Long{
    return if(this != null && this >= 0) this else 0
}