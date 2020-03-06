package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.model.page_renderer

import io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto.PdfPageExportData
import java.io.File

interface PageRenderer {
    /**
     * Saved document
     */
    val document: File?

    /**
     * Render the document and save it to a file
     */
    suspend fun render(sourceData: PdfPageExportData)

    /**
     * Remove the file
     */
    fun remove()
}