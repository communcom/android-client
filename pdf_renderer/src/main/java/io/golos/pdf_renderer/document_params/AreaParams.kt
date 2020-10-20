package io.golos.pdf_renderer.document_params

import androidx.annotation.ColorInt

data class AreaParams (
    @ColorInt
    val fillColor: Int,

    val left: Float,
    val top: Float,

    val width: Float,
    val height: Float,

    val cornerRadius: Float = 0f
)