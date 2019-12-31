package io.golos.cyber_android.ui.screens.login_sign_in_username.model.auth

import io.golos.commun4j.utils.AuthUtils
import io.golos.commun4j.utils.StringSigner
import io.golos.domain.CrashlyticsFacade
import io.golos.domain.DispatchersProvider
import io.golos.data.persistence.key_value_storage.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.AuthResultDomain
import io.golos.domain.dto.AuthStateDomain
import io.golos.domain.dto.AuthType
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.repositories.AuthRepository
import io.golos.domain.repositories.CurrentUserRepository
import io.golos.domain.repositories.UsersRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthUseCaseImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val userKeyStore: UserKeyStore,
    private val usersRepository: UsersRepository,
    private val authRepository: AuthRepository,
    private val crashlytics: CrashlyticsFacade,
    private val keyValueStorage: KeyValueStorageFacade,
    private val currentUserRepository: CurrentUserRepository
) : AuthUseCase {

    override suspend fun auth(userName: String, password: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            val userProfile = usersRepository.getUserProfile(userName)

            val userId = userProfile.userId

            // generates private keys, stores it and returns an active key
            val privateActiveKey = userKeyStore.createKeys(userId, userName, password).activePrivateKey

            checkAuthConditions(privateActiveKey, userName, userId)

            processAuth(userName, privateActiveKey)

            crashlytics.registerUser(userName, userId.userId)

            val authState = saveAuthState(userName, userId)

            currentUserRepository.authState = authState
            currentUserRepository.userAvatarUrl = userProfile.avatarUrl
        }
    }

    /**
     * Throws [IllegalArgumentException] if the checking is failed
     */
    private suspend fun checkAuthConditions(privateActiveKey: String, userName: String, userId: UserIdDomain) {
        // Private active key validation
        withContext(dispatchersProvider.calculationsDispatcher) {
            AuthUtils.checkPrivateWiF(privateActiveKey)
        }

        // Get user profile from BC and check it
        val blockChainProfile = authRepository.getUserBlockChainProfile(userId)
        check(blockChainProfile.accountName.isNotEmpty()) {"BC account for user $userName not found"}

        // Try to get Active permission and check it
        val activePermission = withContext(dispatchersProvider.calculationsDispatcher) {
            blockChainProfile.permissions.find { it.permName == "active" }
        }
        check(activePermission != null) { "BC account for user $userName has no Active permission" }

        // Try to get public active key from server and check it
        val publicActiveKey = activePermission.requiredAuth.keys.firstOrNull()?.key
        check(publicActiveKey != null) {"BC account for user $userName has no Active key"}

        // Check active keys matching
        val isWiFsValid = withContext(dispatchersProvider.calculationsDispatcher) {
            AuthUtils.isWiFsValid(privateActiveKey, publicActiveKey)
        }
        check(isWiFsValid) {"Keys for user $userName are not match"}
    }

    private suspend fun processAuth(userName: String, privateActiveKey: String): AuthResultDomain {
        val authSecret = authRepository.getAuthSecret()
        return authRepository.auth(userName, authSecret, StringSigner.signString(authSecret, privateActiveKey))
    }

    private fun saveAuthState(userName: String, userId: UserIdDomain): AuthStateDomain {
        val oldAuthState = keyValueStorage.getAuthState()

        val newAuthState = AuthStateDomain(
            userName = userName,
            user = userId,
            isUserLoggedIn = true,
            isPinCodeSettingsPassed = oldAuthState?.isPinCodeSettingsPassed ?: false,
            isFingerprintSettingsPassed = oldAuthState?.isFingerprintSettingsPassed ?: false,
            isKeysExported = oldAuthState?.isKeysExported ?: false,
            type = AuthType.SIGN_IN
        )

        keyValueStorage.saveAuthState(newAuthState)

        return newAuthState
    }
}