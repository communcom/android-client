package io.golos.cyber_android.ui.screens.login_activity.signin.qr_code.keys_extractor

import io.golos.cyber4j.sharedmodel.Either
import io.golos.cyber_android.ui.screens.login_activity.signin.qr_code.detector.QrCodeDecrypted
import io.golos.domain.requestmodel.AuthRequestModel

interface QrCodeKeysExtractor {
    suspend fun process(qrCodeData: QrCodeDecrypted): Either<AuthRequestModel, Exception>
}