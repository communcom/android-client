package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.dto

import java.util.*

data class PdfPageData(
    val userName: String,
    val userId: String,

    val createDate: Date,

    val phoneNumber: String,

    val password: String,
    val activeKey: String,
    val ownerKey: String,

    val qrText: String
)