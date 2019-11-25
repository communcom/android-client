package io.golos.cyber_android.ui.screens.profile_photos.model.result_bitmap_calculator

import android.graphics.Bitmap
import android.graphics.Matrix.MTRANS_X
import android.graphics.Matrix.MTRANS_Y
import io.golos.cyber_android.ui.screens.profile_photos.dto.PhotoViewImageInfo
import javax.inject.Inject
import kotlin.math.abs

class ResultBitmapCalculatorImpl
@Inject
constructor() : ResultBitmapCalculator {
    /**
     * Returns preview visible area as a bitmap
     */
    override suspend fun calculateVisibleArea(imageInfo: PhotoViewImageInfo): Bitmap {
        val fullTransferedBitmap = Bitmap.createBitmap(
            imageInfo.source,
            0,
            0,
            imageInfo.source.width,
            imageInfo.source.height,
            imageInfo.imageMatrix,
            true)

        val matrixValues = FloatArray(9)
        imageInfo.imageMatrix.getValues(matrixValues)

        return Bitmap.createBitmap(
            fullTransferedBitmap,
            abs(matrixValues[MTRANS_X]).toInt(),
            abs(matrixValues[MTRANS_Y]).toInt(),
            abs(imageInfo.drawingRect.width()),
            abs(imageInfo.drawingRect.height()),
            null,
            true
        )
    }
}