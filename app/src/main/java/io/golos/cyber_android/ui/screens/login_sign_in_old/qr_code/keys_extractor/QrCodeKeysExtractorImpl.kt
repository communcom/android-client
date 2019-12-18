package io.golos.cyber_android.ui.screens.login_sign_in_old.qr_code.keys_extractor

import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.ui.screens.login_sign_in_old.qr_code.detector.QrCodeDecrypted
import io.golos.domain.DispatchersProvider
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.AuthType
import io.golos.domain.dto.CyberUser
import io.golos.domain.dto.UserKey
import io.golos.domain.dto.UserKeyType
import io.golos.domain.requestmodel.AuthRequestModel
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Generate keys using user's name and master password, stores them and returns credentials for Sign In operation.
 * */
class QrCodeKeysExtractorImpl
@Inject
constructor (
    private val userKeyStore: UserKeyStore,
    private val dispatchersProvider: DispatchersProvider
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

                val model = AuthRequestModel(qrCodeData.userName, CyberUser(""), qrCodeData.activeKey, AuthType.SIGN_IN)
                Either.Success<AuthRequestModel, Exception>(model)
            } catch(ex: Exception) {
                Timber.e(ex)
                Either.Failure<AuthRequestModel, Exception>(ex)
            }
        }
}