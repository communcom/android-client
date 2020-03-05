package io.golos.cyber_android.ui.screens.login_activity.shared.fragments_data_pass

import io.golos.cyber_android.ui.shared.fragments_data_pass.FragmentsDataPassBase
import io.golos.cyber_android.ui.dto.QrCodeContent
import io.golos.domain.dependency_injection.scopes.ActivityScope
import io.golos.domain.dto.CountryDomain
import javax.inject.Inject

@ActivityScope
class LoginActivityFragmentsDataPassImpl
@Inject
constructor():
    FragmentsDataPassBase(),
    LoginActivityFragmentsDataPass {

    private companion object {
        private const val QR_CODE_KEY = 22822231
        private const val SELECTED_COUNTRY_KEY = 907080
        private const val PHONE_KEY = 169548324
    }

    override fun putQrCode(qrCode: QrCodeContent) = put(QR_CODE_KEY, qrCode)

    override fun getQrCode(): QrCodeContent? = get(QR_CODE_KEY) as QrCodeContent?

    override fun putSelectedCountry(country: CountryDomain) = put(SELECTED_COUNTRY_KEY, country)

    override fun getSelectedCountry(): CountryDomain? = get(SELECTED_COUNTRY_KEY) as CountryDomain?

    override fun putPhone(phone: String) = put(PHONE_KEY, phone)

    override fun getPhonePhone(): String? = get(PHONE_KEY) as String?
}