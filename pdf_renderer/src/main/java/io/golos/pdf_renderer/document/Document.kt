package io.golos.pdf_renderer.document

import java.io.Closeable
import java.io.OutputStream

interface Document : Closeable {
    fun addPage(): DocumentPage

    fun writeTo(out: OutputStream)
}