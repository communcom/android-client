package io.golos.pdf_renderer.document_params

data class TextLineParams (
    val textParams: TextParams,

    /**
     * In points (1/72 inch)
     */
    val left: Float,

    /**
     * In points (1/72 inch)
     */
    val top: Float
)