package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.pdf_export

import android.content.Context
import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto.PdfPageExportData
import io.golos.pdf_renderer.document.DocumentImpl
import io.golos.pdf_renderer.page_size.A4PageSize
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

object PdfKeysUtils {
    /**
     * Saves text as pdf document
     * @param dirPath path to directory in which file should be saved
     * @return true if operation was successful, false otherwise
     */
    fun saveTextAsPdfDocument(context: Context, dataToExport: PdfPageExportData, dirPath: String): Boolean =
        try {
            val fileName = getKeysSavePathInDir(dataToExport.userName, dirPath)

            val pageInfo = A4PageSize()

            DocumentImpl.create(context, pageInfo).use { doc ->
                doc.addPage().use { page ->
                    val renderer =
                        PageRenderer(context)
                    renderer.render(page, dataToExport)
                }

                val file = File(fileName)
                FileOutputStream(file).use { stream ->
                    doc.writeTo(stream)
                }
            }
            true
        } catch (ex: Exception) {
            false
        }

    fun getKeysSavePathInDir(userName: String, dirPath: String) = "$dirPath/Commun-private-keys($userName).pdf"
}