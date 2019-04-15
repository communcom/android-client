package io.golos.cyber_android.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import io.golos.cyber_android.R
import io.golos.domain.interactors.model.GeneratedUserKeys
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object PdfKeysUtils {

    private const val PAGE_WIDTH = 600
    private const val PAGE_HEIGHT = 1000
    private const val TEXT_PADDING_X = 40
    private const val TEXT_PADDING_Y = 60
    private const val LINE_HEIGHT = 20

    /**
     * Saves text as pdf document
     * @param content text that should be saved
     * @param dirPath path to directory in which file should be saved
     * @return true if operation was successful, false otherwise
     */
    fun saveTextAsPdfDocument(content: String, dirPath: String) = saveDocument(dirPath, generatePdf(content))

    private fun saveDocument(dirPath: String, pdf: PdfDocument): Boolean {
        val file = File(dirPath)
        if (!file.exists()) {
            file.mkdirs()
        }

        val filePath = File("$dirPath/keys.pdf")
        return try {
            if (filePath.exists()) filePath.delete()
            pdf.writeTo(FileOutputStream(filePath))
            true
        } catch (e: IOException) {
            false
        } finally {
            pdf.close()
        }
    }

    private fun generatePdf(content: String): PdfDocument {
        val pdf = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 0).create()
        val page = pdf.startPage(pageInfo)

        val textPaint = Paint().apply {
            color = Color.BLACK
        }

        val lines = content.split("\n")
        for ((index, line) in lines.withIndex())
            page.canvas.drawText(
                line,
                TEXT_PADDING_X.toFloat(),
                TEXT_PADDING_Y.toFloat() + index * LINE_HEIGHT,
                textPaint
            )

        pdf.finishPage(page)

        return pdf
    }

    fun getKeysSummary(context: Context, keys: GeneratedUserKeys) = String.format(
        context.resources.getString(R.string.keys_format),
        keys.ownerPrivateKey,
        keys.activePrivateKey,
        keys.postingPrivateKey
    )
}