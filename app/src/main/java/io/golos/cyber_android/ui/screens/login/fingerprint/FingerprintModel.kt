package io.golos.cyber_android.ui.screens.login.fingerprint

import io.golos.domain.entities.AppUnlockWay

interface FingerprintModel {
    /**
     * @return true in case of success
     */
    suspend fun saveAppUnlockWay(unlockWay: AppUnlockWay): Boolean
}