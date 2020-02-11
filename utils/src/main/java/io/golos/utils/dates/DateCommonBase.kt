package io.golos.utils.dates

data class DateCommonBase(
    /**
     * Number of the day
     */
    val base: Int
) {
    operator fun minus(value: DateCommonBase): Int = base - value.base
}