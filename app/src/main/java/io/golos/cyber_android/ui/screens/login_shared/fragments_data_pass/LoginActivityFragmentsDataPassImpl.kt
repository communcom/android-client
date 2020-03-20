package io.golos.cyber_android.ui.screens.login_shared.fragments_data_pass

import io.golos.cyber_android.ui.shared.fragments_data_pass.FragmentsDataPassBase
import io.golos.cyber_android.ui.dto.QrCodeContent
import io.golos.domain.dependency_injection.scopes.ActivityScope
import io.golos.domain.dto.CountryDomain
import io.golos.domain.dto.SignUpIdentityDomain
import io.golos.domain.dto.UserIdDomain
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
        private const val FTUE_BONUS_KEY = 17816978
        private const val PASSWORD_KEY = 8490421
        private const val USER_NAME_KEY = 37148304
        private const val USER_ID_KEY = 20028776
        private const val IDENTITY_KEY = 96790846
    }

    override fun putQrCode(qrCode: QrCodeContent) = put(QR_CODE_KEY, qrCode)

    override fun getQrCode(): QrCodeContent? = get(QR_CODE_KEY) as QrCodeContent?

    override fun putSelectedCountry(country: CountryDomain) = put(SELECTED_COUNTRY_KEY, country)

    override fun getSelectedCountry(): CountryDomain? = get(SELECTED_COUNTRY_KEY) as CountryDomain?

    override fun putPhone(phone: String) = put(PHONE_KEY, phone)

    override fun getPhone(): String? = get(PHONE_KEY) as String?

    override fun putUserName(name: String) = put(USER_NAME_KEY, name)

    override fun getUserName(): String? = get(USER_NAME_KEY) as String?

    override fun putUserId(userId: UserIdDomain) = put(USER_ID_KEY, userId)

    override fun getUserId(): UserIdDomain? = get(USER_ID_KEY) as UserIdDomain?

    override fun putFtueCommunityBonus(bonus: Int) = put(FTUE_BONUS_KEY, bonus)

    override fun getFtueCommunityBonus(): Int? = get(FTUE_BONUS_KEY) as Int?

    override fun putPassword(password: String) = put(PASSWORD_KEY, password)

    override fun getPassword(): String? = get(PASSWORD_KEY) as String?

    override fun putIdentity(identity: SignUpIdentityDomain) = put(IDENTITY_KEY, identity)

    override fun getIdentity(): SignUpIdentityDomain? = get(IDENTITY_KEY) as SignUpIdentityDomain?
}