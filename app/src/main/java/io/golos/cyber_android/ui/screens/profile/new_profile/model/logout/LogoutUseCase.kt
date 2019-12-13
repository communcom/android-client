package io.golos.cyber_android.ui.screens.profile.new_profile.model.logout

interface LogoutUseCase {
    suspend fun logout()

    fun restartApp()
}