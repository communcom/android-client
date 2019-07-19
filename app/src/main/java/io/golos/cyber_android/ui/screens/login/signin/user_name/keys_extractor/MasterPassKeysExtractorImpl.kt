package io.golos.cyber_android.ui.screens.login.signin.user_name.keys_extractor

import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.UserKeyStore
import io.golos.domain.entities.CyberUser
import io.golos.domain.requestmodel.AuthRequestModel
import io.golos.sharedmodel.Either
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Generate keys using user's name and master password, stores them and returns credentials for Sign In operation.
 * */
class MasterPassKeysExtractorImpl
@Inject
constructor (
    private val userKeyStore: UserKeyStore,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger
) : MasterPassKeysExtractor {

    override suspend fun process(userName: String, masterKey: String): Either<AuthRequestModel, Exception> =
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                val activeKey = userKeyStore.createKeys(userName, masterKey).activePrivateKey
                Either.Success<AuthRequestModel, Exception>(AuthRequestModel(CyberUser(userName), activeKey))
            } catch(ex: Exception) {
                logger.invoke(ex)
                Either.Failure<AuthRequestModel, Exception>(ex)
            }
        }
}