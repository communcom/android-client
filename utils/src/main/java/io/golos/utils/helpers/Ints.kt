package io.golos.utils.helpers

fun Int?.positiveValue(): Int {
    return if (this != null && this >= 0) this else 0
}

fun Int.toPluralInt(): Int{
    return if (this > 10) 10 else this
}

fun Long.toPluralInt(): Int{
    return if (this > 10) 10 else this.toInt()
}

fun Long?.positiveValue(): Long{
    return if(this != null && this >= 0) this else 0
}