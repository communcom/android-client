package io.golos.data.repositories.sign_up_tokens

interface SignUpTokensRepository {
    suspend fun getGoogleIdentity(accessToken: String): String

    suspend fun getFacebookIdentity(accessToken: String): String
}