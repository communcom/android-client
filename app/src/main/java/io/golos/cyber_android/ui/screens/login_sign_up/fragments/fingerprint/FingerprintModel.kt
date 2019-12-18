package io.golos.cyber_android.ui.screens.login_sign_up.fragments.fingerprint

import io.golos.domain.dto.AppUnlockWay
import io.golos.domain.dto.AuthType

interface FingerprintModel {
    /**
     * @return true in case of success
     */
    suspend fun saveAppUnlockWay(unlockWay: AppUnlockWay): Boolean

    suspend fun getAuthType(): AuthType
}