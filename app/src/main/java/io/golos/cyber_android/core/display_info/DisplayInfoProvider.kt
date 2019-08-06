package io.golos.cyber_android.core.display_info

import android.util.Size

/** */
interface DisplayInfoProvider {
    /** */
    val sizeInPix: Size

    /** */
    val sizeInDp: Size

    /** */
    val sizeCategory: DisplaySizeCategory

    /** */
    val densityCategory: DisplayDensityCategory
}