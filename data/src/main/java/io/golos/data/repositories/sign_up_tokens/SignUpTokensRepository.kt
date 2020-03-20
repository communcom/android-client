package io.golos.data.repositories.sign_up_tokens

import io.golos.domain.dto.SignUpIdentityDomain

interface SignUpTokensRepository {
    suspend fun getGoogleIdentity(accessToken: String): SignUpIdentityDomain

    suspend fun getFacebookIdentity(accessToken: String): SignUpIdentityDomain
}