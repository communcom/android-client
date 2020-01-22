package io.golos.cyber_android.ui.screens.login_sign_up_countries.model

import io.golos.cyber_android.ui.shared.countries.CountriesRepository
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.dto.CountryDomain
import timber.log.Timber
import javax.inject.Inject

class SignUpCountryModelImpl
@Inject
constructor(
    private val countriesRepository: CountriesRepository
) : ModelBaseImpl(), SignUpCountryModel {

    override suspend fun getCountries(): List<CountryDomain> =
        try {
            countriesRepository.getCountries()
        } catch (ex: Exception) {
            Timber.e(ex)
            throw ex
        }

    override fun search(query: String): List<CountryDomain> = countriesRepository.search(query)
}