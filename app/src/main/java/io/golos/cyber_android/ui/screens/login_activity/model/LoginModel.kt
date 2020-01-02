package io.golos.cyber_android.ui.screens.login_activity.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase

interface LoginModel: ModelBase {
    val hasNetworkConnection: Boolean

    val isSetupCompleted: Boolean

    suspend fun hasAuthState(): Boolean

    fun closeApp()

    suspend fun login(): Boolean
}