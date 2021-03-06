package io.golos.use_cases.auth

import io.golos.commun4j.utils.AuthUtils
import io.golos.commun4j.utils.StringSigner
import io.golos.domain.CrashlyticsFacade
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.*
import io.golos.domain.repositories.*
import kotlinx.coroutines.withContext
import timber.log.Timber
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
    private val currentUserRepository: CurrentUserRepository,
    private val notificationsRepository: NotificationsRepository,
    private val globalSettingsRepository: GlobalSettingsRepository
) : AuthUseCase {

    override suspend fun auth(userName: String, password: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            val userProfile = usersRepository.getUserProfile(userName)

            val userId = userProfile.userId

            // generates private keys, stores it and returns an active key
            val privateActiveKey = userKeyStore.createAndSaveKeys(userId, userName, password).activePrivateKey

            checkAuthConditions(privateActiveKey, userName, userId)

            processAuth(userName, privateActiveKey)

            crashlytics.registerUser(userName, userId.userId)

            val authState = saveAuthState(userName, userId)

            sendFsmToken()

            currentUserRepository.authState = authState
            currentUserRepository.userAvatarUrl = userProfile.avatarUrl

            globalSettingsRepository.loadValues()
        }
    }

    override suspend fun authBrief(userName: String, activePrivateKey: String?) {
        processAuth(userName, activePrivateKey ?: userKeyStore.getKey(UserKeyType.ACTIVE))
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
        Timber.tag("PRIVATE_ACTIVE_KEY").d(privateActiveKey)
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
            isKeysExported = true,
            type = AuthType.SIGN_IN
        )

        keyValueStorage.saveAuthState(newAuthState)

        return newAuthState
    }

    private suspend fun sendFsmToken() {
        notificationsRepository.setTimeZoneOffset()

        keyValueStorage.getFcmToken()
            ?.let { tokenInfo ->
                Timber.tag("FCM_MESSAGES").d("Token: ${tokenInfo.token}")
                Timber.tag("FCM_MESSAGES").d("Token sent: ${tokenInfo.sent}")

                if(!tokenInfo.sent) {
                    notificationsRepository.setFcmToken(tokenInfo.token)
                    keyValueStorage.saveFcmToken(tokenInfo.copy(sent = true))
                }
            }
    }
}