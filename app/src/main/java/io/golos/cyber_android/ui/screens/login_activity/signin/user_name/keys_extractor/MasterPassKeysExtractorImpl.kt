package io.golos.cyber_android.ui.screens.login_activity.signin.user_name.keys_extractor

import io.golos.commun4j.sharedmodel.Either
import io.golos.domain.DispatchersProvider
import io.golos.domain.UserKeyStore
import io.golos.domain.api.AuthApi
import io.golos.domain.entities.AuthType
import io.golos.domain.entities.CyberUser
import io.golos.domain.requestmodel.AuthRequestModel
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Generate keys using user's name and master password, stores them and returns credentials for Sign In operation.
 * */
class MasterPassKeysExtractorImpl
@Inject
constructor (
    private val userKeyStore: UserKeyStore,
    private val dispatchersProvider: DispatchersProvider,
    private val authApi: AuthApi
) : MasterPassKeysExtractor {

    override suspend fun process(userName: String, masterKey: String): Either<AuthRequestModel, Exception> =
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                val userId = authApi.getUserProfile(userName).userId.name
                val activeKey = userKeyStore.createKeys(userId, userName, masterKey).activePrivateKey
                val model = AuthRequestModel(userName, CyberUser(userName), activeKey, AuthType.SIGN_IN)
                Either.Success<AuthRequestModel, Exception>(model)
            } catch(ex: Exception) {
                Timber.e(ex)
                Either.Failure<AuthRequestModel, Exception>(ex)
            }
        }
}