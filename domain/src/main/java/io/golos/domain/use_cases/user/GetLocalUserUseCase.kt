package io.golos.domain.use_cases.user

import io.golos.domain.dto.UserDomain

interface GetLocalUserUseCase {

    suspend fun getLocalUser(): UserDomain
}