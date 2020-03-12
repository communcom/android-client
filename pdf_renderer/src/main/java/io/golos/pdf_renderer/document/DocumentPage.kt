package io.golos.pdf_renderer.document

import io.golos.pdf_renderer.document_params.AreaParams
import io.golos.pdf_renderer.document_params.QrParams
import io.golos.pdf_renderer.document_params.TextLineParams
import io.golos.pdf_renderer.document_params.TextParagraphParams
import io.golos.pdf_renderer.page_size.PageSize
import java.io.Closeable

interface DocumentPage : Closeable {
    val pageSize: PageSize

    fun drawTextLine(text: String, params: TextLineParams)

    fun drawTextParagraph(text: String, params: TextParagraphParams)

    fun drawArea(params: AreaParams)

    fun drawQr(text: String, params: QrParams)
}