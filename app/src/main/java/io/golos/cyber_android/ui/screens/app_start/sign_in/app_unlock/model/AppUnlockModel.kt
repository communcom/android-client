package io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.model

import io.golos.domain.dto.AppUnlockWay

interface AppUnlockModel {
    suspend fun saveAppUnlockWay(unlockWay: AppUnlockWay)

    suspend fun removeSignUpSnapshot() {}
}