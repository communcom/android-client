package io.golos.cyber_android.ui.screens.app_start.sign_up.phone.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.CountryDomain

interface SignUpPhoneModel : ModelBase {
    val country: CountryDomain?

    suspend fun getCurrentCountry(): CountryDomain?

    /**
     * @return selected country or null is a country is not updated
     */
    fun updateCurrentCountry(newCountry: CountryDomain?): CountryDomain?

    fun isPhoneValid(phoneText: String): Boolean
}