package io.golos.cyber_android.application.shared.display_info

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.util.Size
import io.golos.utils.helpers.minIndexBy
import kotlin.math.abs

/** */
object DisplayInfoProvider {
    /** */
    fun getSizeInPix(context: Context): Size =
        context.resources.displayMetrics.let {
                Size(it.widthPixels, it.heightPixels)
            }

    /** */
    fun getSizeInDp(context: Context): Size =
        context.resources.displayMetrics.let {
            Size((it.widthPixels / it.density).toInt(), (it.heightPixels / it.density).toInt())
        }

    /** */
    fun getSizeCategory(context: Context): DisplaySizeCategory =
        context.resources.configuration.screenLayout.let {
            when(it and Configuration.SCREENLAYOUT_SIZE_MASK) {
                Configuration.SCREENLAYOUT_SIZE_XLARGE -> DisplaySizeCategory.XLARGE
                Configuration.SCREENLAYOUT_SIZE_LARGE -> DisplaySizeCategory.LARGE
                Configuration.SCREENLAYOUT_SIZE_NORMAL -> DisplaySizeCategory.NORMAL
                Configuration.SCREENLAYOUT_SIZE_SMALL -> DisplaySizeCategory.SMALL
                else -> DisplaySizeCategory.UNDEFINED
            }
        }

    /** */
    @Suppress("MoveVariableDeclarationIntoWhen")
    fun getDensityCategory(context: Context): DisplayDensityCategory =
        context.resources.displayMetrics.let { metrics ->
            val defaultScale = 1F / DisplayMetrics.DENSITY_DEFAULT

            val densityFactors = listOf(
                DisplayMetrics.DENSITY_LOW,
                DisplayMetrics.DENSITY_MEDIUM,
                DisplayMetrics.DENSITY_HIGH,
                DisplayMetrics.DENSITY_XHIGH,
                DisplayMetrics.DENSITY_XXHIGH,
                DisplayMetrics.DENSITY_XXXHIGH)

            val densityIndex = densityFactors.minIndexBy {
                densityFactor, _ -> abs(densityFactor * defaultScale - metrics.scaledDensity)
            }

            return when(densityIndex) {
                0 -> DisplayDensityCategory.LDPI
                1 -> DisplayDensityCategory.MDPI
                2 -> DisplayDensityCategory.HDPI
                3 -> DisplayDensityCategory.XHDPI
                4 -> DisplayDensityCategory.XXHDPI
                5 -> DisplayDensityCategory.XXXHDPI
                else -> throw UnsupportedOperationException("Invalid density index: $densityIndex")
            }
        }
}