package io.golos.cyber_android.ui.common.keys_to_pdf

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import io.golos.cyber_android.R
import io.golos.domain.entities.UserKey
import io.golos.domain.entities.UserKeyType
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object PdfKeysUtils {

    private const val PAGE_WIDTH = 640
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
    fun saveTextAsPdfDocument(content: String, dirPath: String) =
        saveDocument(
            dirPath,
            generatePdf(content)
        )

    fun getKeysSavePathInDir(dirPath: String) = "$dirPath/keys.pdf"

    private fun saveDocument(dirPath: String, pdf: PdfDocument): Boolean {
        val file = File(dirPath)
        if (!file.exists()) {
            file.mkdirs()
        }

        val filePath = File(getKeysSavePathInDir(dirPath))
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

        val pageInfo = PdfDocument.PageInfo.Builder(
            PAGE_WIDTH,
            PAGE_HEIGHT, 0).create()
        val page = pdf.startPage(pageInfo)

        val textPaint = Paint().apply {
            color = Color.BLACK

            // Don't remove this line!
            // It used for fixing this issue: https://stackoverflow.com/questions/56282273/text-in-pdf-file-has-mysterious-spaces
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
        }

        val lines = content.split("\n")
        for ((index, line) in lines.withIndex()) {

            page.canvas.drawText(
                line,
                TEXT_PADDING_X.toFloat(),
                TEXT_PADDING_Y.toFloat() + index * LINE_HEIGHT,
                textPaint
            )
        }

        pdf.finishPage(page)

        return pdf
    }

    fun getKeysSummary(context: Context, userName: String, userId: String, keys: List<UserKey>) = String.format(
        context.resources.getString(R.string.keys_format),
        keys.singleOrNull { it.keyType == UserKeyType.MASTER }?.key ?: "",
        userName,
        keys.single { it.keyType == UserKeyType.OWNER }.key,
        keys.single { it.keyType == UserKeyType.ACTIVE }.key,
        keys.single { it.keyType == UserKeyType.POSTING }.key,
        keys.single { it.keyType == UserKeyType.MEMO }.key,
        userId
    )
}