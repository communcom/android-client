package io.golos.domain.repositories

import io.golos.domain.dto.AuthResultDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.bc_profile.BCProfileDomain

interface AuthRepository {
    suspend fun auth(userName: String, secret: String, signedSecret: String): AuthResultDomain

    suspend fun getAuthSecret(): String

    suspend fun getUserBlockChainProfile(userId: UserIdDomain): BCProfileDomain

    suspend fun writeUserToBlockChain(phone: String, userId: String, userName: String, owner: String, active: String)
}