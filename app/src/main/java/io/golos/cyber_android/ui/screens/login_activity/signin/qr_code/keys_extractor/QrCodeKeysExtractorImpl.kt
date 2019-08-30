package io.golos.cyber_android.ui.screens.login_activity.signin.qr_code.keys_extractor

import io.golos.cyber4j.sharedmodel.Either
import io.golos.cyber_android.ui.screens.login_activity.signin.qr_code.detector.QrCodeDecrypted
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.UserKeyStore
import io.golos.domain.entities.AuthType
import io.golos.domain.entities.CyberUser
import io.golos.domain.entities.UserKey
import io.golos.domain.entities.UserKeyType
import io.golos.domain.requestmodel.AuthRequestModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Generate keys using user's name and master password, stores them and returns credentials for Sign In operation.
 * */
class QrCodeKeysExtractorImpl
@Inject
constructor (
    private val userKeyStore: UserKeyStore,
    private val dispatchersProvider: DispatchersProvider,
    private val logger: Logger
) : QrCodeKeysExtractor {

    override suspend fun process(qrCodeData: QrCodeDecrypted): Either<AuthRequestModel, Exception> =
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                val keys = listOf(
                    UserKey(UserKeyType.OWNER, qrCodeData.ownerKey),
                    UserKey(UserKeyType.ACTIVE, qrCodeData.activeKey),
                    UserKey(UserKeyType.POSTING, qrCodeData.postingKey),
                    UserKey(UserKeyType.MEMO, qrCodeData.memoKey)
                )

                userKeyStore.updateKeys(keys)

                val model = AuthRequestModel(CyberUser(qrCodeData.userName), qrCodeData.activeKey, AuthType.SIGN_IN)
                Either.Success<AuthRequestModel, Exception>(model)
            } catch(ex: Exception) {
                logger.log(ex)
                Either.Failure<AuthRequestModel, Exception>(ex)
            }
        }
}