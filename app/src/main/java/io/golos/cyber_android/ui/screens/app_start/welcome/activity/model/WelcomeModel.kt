package io.golos.cyber_android.ui.screens.app_start.welcome.activity.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase

interface WelcomeModel: ModelBase {
    val hasNetworkConnection: Boolean

    suspend fun hasAuthState(): Boolean

    fun closeApp()

    suspend fun login()

    suspend fun isOutdated(): Boolean
}