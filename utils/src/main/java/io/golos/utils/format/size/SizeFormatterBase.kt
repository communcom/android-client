package io.golos.utils.format.size

import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

abstract class SizeFormatterBase(private val calculationBase: Double) {
    fun calculateUnitIndex(size: Int): Int =
        if(size <= 0)
            0
        else
            (log10(size.toDouble()) / log10(calculationBase)).toInt()

    fun calculateValue(template: String, size: Int, unitIndex: Int) =
        DecimalFormat(template).format(size/ calculationBase.pow(unitIndex))
}