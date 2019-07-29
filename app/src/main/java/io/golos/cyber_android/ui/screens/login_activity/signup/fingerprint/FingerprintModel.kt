package io.golos.cyber_android.ui.screens.login_activity.signup.fingerprint

import io.golos.domain.entities.AppUnlockWay
import io.golos.domain.entities.AuthType

interface FingerprintModel {
    /**
     * @return true in case of success
     */
    suspend fun saveAppUnlockWay(unlockWay: AppUnlockWay): Boolean

    suspend fun getAuthType(): AuthType
}