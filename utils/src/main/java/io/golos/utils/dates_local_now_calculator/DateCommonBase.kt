package io.golos.utils.dates_local_now_calculator

data class DateCommonBase(
    /**
     * Number of the day
     */
    val base: Int
) {
    operator fun minus(value: DateCommonBase): Int = base - value.base
}