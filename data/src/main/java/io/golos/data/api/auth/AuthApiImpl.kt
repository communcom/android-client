package io.golos.data.api.auth

import io.golos.commun4j.Commun4j
import io.golos.commun4j.model.AuthType
import io.golos.commun4j.model.UserProfile
import io.golos.commun4j.services.model.AuthResult
import io.golos.commun4j.sharedmodel.AuthSecret
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.utils.Pair
import io.golos.data.api.Commun4jApiBase
import io.golos.domain.repositories.CurrentUserRepositoryRead
import io.golos.domain.api.AuthApi
import javax.inject.Inject

class AuthApiImpl
@Inject
constructor(
    commun4j: Commun4j,
    currentUserRepository: CurrentUserRepositoryRead
) : Commun4jApiBase(commun4j, currentUserRepository), AuthApi {

    override fun getUserProfile(userName: String, user: CyberName?) = commun4j.getUserProfile(user, userName).getOrThrow()

    override fun getUserAccount(user: CyberName): UserProfile = commun4j.getUserAccount(user).getOrThrow()

    override fun getAuthSecret(): AuthSecret = commun4j.getAuthSecret().getOrThrow()

    override fun authWithSecret(user: String, cyberName: CyberName, secret: String, signedSecret: String): AuthResult {
        return try {
            commun4j.authWithSecret(user, secret, signedSecret).getOrThrow()
        } catch (ex: Exception) {
            // It's a dirty fix for 0.8.7 library
            AuthResult(user, user, cyberName, "")
        }
    }

    override fun setActiveUserCreds(user: CyberName, activeKey: String) =
        commun4j.keyStorage.addAccountKeys(user, setOf(Pair(AuthType.ACTIVE, activeKey)))
}