package io.golos.cyber_android.ui.screens.login_sign_up_countries.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase
import io.golos.domain.dto.CountryDomain

interface SignUpCountryModel : ModelBase {
    suspend fun getCountries(): List<CountryDomain>

    fun search(query: String): List<CountryDomain>
}