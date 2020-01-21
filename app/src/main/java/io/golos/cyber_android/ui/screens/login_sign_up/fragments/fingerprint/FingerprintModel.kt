package io.golos.cyber_android.ui.screens.login_sign_up.fragments.fingerprint

import io.golos.domain.dto.AppUnlockWay
import io.golos.domain.dto.AuthType

interface FingerprintModel {
    suspend fun saveAppUnlockWay(unlockWay: AppUnlockWay)

    suspend fun saveKeysExported()

    suspend fun getAuthType(): AuthType
}