package io.golos.cyber_android.ui.screens.app_start.sign_in.shared.data_pass

import io.golos.cyber_android.ui.dto.QrCodeContent
import io.golos.cyber_android.ui.shared.fragments_data_pass.FragmentsDataPassBase
import io.golos.domain.dependency_injection.scopes.ActivityScope
import javax.inject.Inject

@ActivityScope
class SignInFragmentsDataPassImpl
@Inject
constructor(): FragmentsDataPassBase(), SignInFragmentsDataPass {
    private companion object {
        private const val QR_CODE_KEY = 22822231
    }

    override fun putQrCode(qrCode: QrCodeContent) = put(QR_CODE_KEY, qrCode)

    override fun getQrCode(): QrCodeContent? = get(QR_CODE_KEY) as QrCodeContent?
}