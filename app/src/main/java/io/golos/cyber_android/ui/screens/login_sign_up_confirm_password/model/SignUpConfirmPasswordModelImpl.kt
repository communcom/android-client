package io.golos.cyber_android.ui.screens.login_sign_up_confirm_password.model

import android.content.Context
import io.golos.commun4j.utils.StringSigner
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.login_shared.fragments_data_pass.LoginActivityFragmentsDataPass
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.dto.PasswordProcessingResult
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.model.SignUpCreatePasswordModel
import io.golos.cyber_android.ui.screens.login_sign_up_create_password.model.password_validator.PasswordValidator
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.CrashlyticsFacade
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.AuthResultDomain
import io.golos.domain.dto.AuthStateDomain
import io.golos.domain.dto.AuthType
import io.golos.domain.dto.FtueBoardStageEntity
import io.golos.domain.repositories.AuthRepository
import io.golos.domain.repositories.CurrentUserRepository
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SignUpConfirmPasswordModelImpl
@Inject
constructor(
    appContext: Context,
    private val dispatchersProvider: DispatchersProvider,
    override val passwordValidator: PasswordValidator,
    private val dataPass: LoginActivityFragmentsDataPass,
    private val keyValueStorage: KeyValueStorageFacade,
    private val authRepository: AuthRepository,
    private val currentUserRepository: CurrentUserRepository,
    private val userKeyStore: UserKeyStore,
    private val crashlytics: CrashlyticsFacade
) : SignUpCreatePasswordModel,
    ModelBaseImpl() {

    override val screenTitle: String = appContext.getString(R.string.confirm_password)
    override val passwordHint: String = appContext.getString(R.string.reenter_your_password)

    override suspend fun processPassword(password: String): PasswordProcessingResult {
        val createdPassword = dataPass.getPassword()!!
        if(password == createdPassword) {
            savePassword(password)
            return PasswordProcessingResult.SUCCESS
        }
        return PasswordProcessingResult.PASSWORD_IS_NOT_CONFIRMED
    }

    private suspend fun savePassword(password: String) {
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                val userName = dataPass.getUserName()!!
                val userId = dataPass.getUserId()!!
                val phone = dataPass.getPhone()!!

                // The keys are generated and stored to a local drive here
                val userKeys = userKeyStore.createKeys(userId, userName, password)

                // Put users to block chain
                authRepository.writeUserToBlockChain(phone, userId.userId, userName, userKeys.ownerPublicKey, userKeys.activePublicKey)

                // Save auth state
                val authState = AuthStateDomain(
                    userName = userName,
                    user = userId,
                    isUserLoggedIn = true,
                    isPinCodeSettingsPassed = false,
                    isFingerprintSettingsPassed = false,
                    isKeysExported = true,
                    type = AuthType.SIGN_UP
                )
                keyValueStorage.saveAuthState(authState)

                // Init current user's repository
                currentUserRepository.authState = authState

                // Need to show ftue
                keyValueStorage.saveFtueBoardStage(FtueBoardStageEntity.NEED_SHOW)

                // Register user in Crashlytics
                crashlytics.registerUser(userName, userId.userId)

                // Authentication
                processAuth(userName, userKeys.activePrivateKey)
            } catch (ex: Exception) {
                Timber.e(ex)
                throw ex
            }
        }
    }

    private suspend fun processAuth(userName: String, privateActiveKey: String): AuthResultDomain {
        val authSecret = authRepository.getAuthSecret()
        return authRepository.auth(userName, authSecret, StringSigner.signString(authSecret, privateActiveKey))
    }
}