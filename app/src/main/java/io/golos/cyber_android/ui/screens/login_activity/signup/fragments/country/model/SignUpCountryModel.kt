package io.golos.cyber_android.ui.screens.login_activity.signup.fragments.country.model

import io.golos.commun4j.sharedmodel.Either
import io.golos.domain.dto.CountryEntity

interface SignUpCountryModel {
    suspend fun getCountries(): Either<List<CountryEntity>, Exception>

    suspend fun search(query: String): List<CountryEntity>
}