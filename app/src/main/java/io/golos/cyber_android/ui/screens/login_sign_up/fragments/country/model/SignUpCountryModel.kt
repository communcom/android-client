package io.golos.cyber_android.ui.screens.login_sign_up.fragments.country.model

import io.golos.commun4j.sharedmodel.Either
import io.golos.domain.dto.CountryEntity

interface SignUpCountryModel {
    suspend fun getCountries(): Either<List<CountryEntity>, Exception>

    suspend fun search(query: String): List<CountryEntity>
}