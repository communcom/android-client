package io.golos.data.repositories.sign_up_tokens

interface SignUpTokensRepository {
    suspend fun getGoogleAccessToken(authCode: String)
}