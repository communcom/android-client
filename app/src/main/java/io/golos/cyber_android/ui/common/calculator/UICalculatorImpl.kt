package io.golos.cyber_android.ui.common.calculator

import android.content.Context

class UICalculatorImpl(private val appContext: Context): UICalculator {
    /**
     * Returns pixels value by dip value
     */
    override fun dpToPixels(dpValue: Float): Int = Math.round(dpValue * getDisplayDensity())

    private fun getDisplayDensity(): Float = appContext.resources.displayMetrics.density
}