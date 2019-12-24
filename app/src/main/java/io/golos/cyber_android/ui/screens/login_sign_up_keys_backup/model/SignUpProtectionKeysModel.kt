package io.golos.cyber_android.ui.screens.login_sign_up_keys_backup.model

import io.golos.cyber_android.ui.common.mvvm.model.ModelBase
import io.golos.domain.dto.UserKey

interface SignUpProtectionKeysModel : ModelBase {
    val allKeys: List<UserKey>

    suspend fun loadKeys()
}