package io.golos.cyber_android.ui.common.calculator

interface UICalculator {
    /**
     * Returns pixels value by dip value
     */
    fun dpToPixels(dpValue: Float): Int
}