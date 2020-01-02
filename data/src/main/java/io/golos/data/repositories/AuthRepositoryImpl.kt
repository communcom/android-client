package io.golos.data.repositories

import android.content.Context
import io.golos.commun4j.Commun4j
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.data.mappers.mapToAuthResultDomain
import io.golos.data.mappers.mapToBCProfileDomain
import io.golos.data.network_state.NetworkStateChecker
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.AuthResultDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.dto.bc_profile.BCProfileDomain
import io.golos.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl
@Inject constructor(
    dispatchersProvider: DispatchersProvider,
    networkStateChecker: NetworkStateChecker,
    private val commun4j: Commun4j
) : RepositoryBase(dispatchersProvider, networkStateChecker),
    AuthRepository {

    override suspend fun auth(userName: String, secret: String, signedSecret: String): AuthResultDomain =
        apiCall { commun4j.authWithSecret(userName, secret, signedSecret) }.mapToAuthResultDomain()

    override suspend fun getAuthSecret(): String = apiCall { commun4j.getAuthSecret() }.secret

    override suspend fun getUserBlockChainProfile(userId: UserIdDomain): BCProfileDomain =
        apiCallChain { commun4j.getUserAccount(CyberName(userId.userId)) }.mapToBCProfileDomain()
}