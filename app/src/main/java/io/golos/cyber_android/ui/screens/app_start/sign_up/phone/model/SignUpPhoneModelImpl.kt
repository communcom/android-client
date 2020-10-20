package io.golos.cyber_android.ui.screens.app_start.sign_up.phone.model

import io.golos.cyber_android.ui.shared.countries.CountriesRepository
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.dto.CountryDomain
import javax.inject.Inject

class SignUpPhoneModelImpl
@Inject
constructor(
    private val countriesRepository: CountriesRepository
) : ModelBaseImpl(), SignUpPhoneModel {
    override var country: CountryDomain? = null

    override suspend fun getCurrentCountry(): CountryDomain? = countriesRepository.getCurrentCountry()?.also { country = it }

    /**
     * @return selected country or null is a country is not updated
     */
    override fun updateCurrentCountry(newCountry: CountryDomain?): CountryDomain? =
        when (newCountry) {
            null,
            country -> null

            else -> {
                country = newCountry
                country
            }
        }

    override fun isPhoneValid(phoneText: String) = phoneText.length >= 18
}