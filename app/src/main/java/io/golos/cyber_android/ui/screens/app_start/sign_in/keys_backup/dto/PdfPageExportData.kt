package io.golos.cyber_android.ui.screens.app_start.sign_in.keys_backup.dto

import java.util.*

data class PdfPageExportData(
    val userName: String,
    val userId: String,

    val createDate: Date,

    val phoneNumber: String,

    val password: String,
    val activeKey: String,
    val ownerKey: String,

    val qrText: String
)