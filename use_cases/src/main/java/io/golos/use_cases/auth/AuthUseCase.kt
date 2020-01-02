package io.golos.use_cases.auth

interface AuthUseCase {
    suspend fun auth(userName: String, password: String)
}