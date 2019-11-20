package io.golos.cyber_android.core.display_info

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.util.Size
import io.golos.utils.minIndexBy
import javax.inject.Inject
import kotlin.math.abs

/** */
class DisplayInfoProviderImpl
@Inject
constructor(
    private val appContext: Context
) : DisplayInfoProvider {
    /** */
    override val sizeInPix: Size
        get() = appContext.resources.displayMetrics.let {
                Size(it.widthPixels, it.heightPixels)
            }

    /** */
    override val sizeInDp: Size
        get() = appContext.resources.displayMetrics.let {
            Size((it.widthPixels / it.density).toInt(), (it.heightPixels / it.density).toInt())
        }

    /** */
    override val sizeCategory: DisplaySizeCategory
        get() = appContext.resources.configuration.screenLayout.let {
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
    override val densityCategory: DisplayDensityCategory
        get() = appContext.resources.displayMetrics.let { metrics ->
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