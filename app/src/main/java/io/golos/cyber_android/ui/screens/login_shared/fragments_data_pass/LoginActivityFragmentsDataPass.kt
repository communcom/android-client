package io.golos.cyber_android.ui.screens.login_shared.fragments_data_pass

import io.golos.cyber_android.ui.dto.QrCodeContent
import io.golos.domain.dto.CountryDomain
import io.golos.domain.dto.UserIdDomain

interface LoginActivityFragmentsDataPass {
    fun putQrCode(qrCode: QrCodeContent)
    fun getQrCode(): QrCodeContent?

    fun putSelectedCountry(country: CountryDomain)
    fun getSelectedCountry(): CountryDomain?

    fun putPhone(phone: String)
    fun getPhone(): String?

    fun putUserName(name: String)
    fun getUserName(): String?

    fun putUserId(userId: UserIdDomain)
    fun getUserId(): UserIdDomain?

    fun putFtueCommunityBonus(bonus: Int)
    fun getFtueCommunityBonus(): Int?

    fun putPassword(password: String)
    fun getPassword(): String?
}