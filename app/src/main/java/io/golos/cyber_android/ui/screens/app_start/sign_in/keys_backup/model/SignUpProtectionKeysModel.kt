package io.golos.cyber_android.ui.screens.app_start.sign_in.keys_backup.model

import io.golos.cyber_android.ui.screens.app_start.sign_in.keys_backup.dto.PdfPageExportData
import io.golos.cyber_android.ui.screens.app_start.sign_in.keys_backup.model.page_renderer.PageRenderer
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.UserKey
import java.io.File

interface SignUpProtectionKeysModel : ModelBase {
    val pageRenderer: PageRenderer

    val allKeys: List<UserKey>

    suspend fun loadKeys()

    suspend fun saveKeysExported()

    fun getDataForExporting(): PdfPageExportData

    suspend fun copyExportedDocumentTo(exportPath: String): File

    suspend fun auth()
}