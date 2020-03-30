package io.golos.domain

import io.golos.domain.dto.SignUpIdentityDomain

interface SignUpTokensRepository {
    suspend fun getGoogleIdentity(accessToken: String): SignUpIdentityDomain

    suspend fun getFacebookIdentity(accessToken: String): SignUpIdentityDomain
}