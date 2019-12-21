package io.golos.cyber_android.ui.screens.login_sign_in_username.model.auth

interface AuthUseCase {
    suspend fun auth(userName: String, password: String)
}