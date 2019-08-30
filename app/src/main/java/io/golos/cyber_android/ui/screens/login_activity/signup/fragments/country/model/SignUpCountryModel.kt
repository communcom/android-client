package io.golos.cyber_android.ui.screens.login_activity.signup.fragments.country.model

import io.golos.cyber4j.sharedmodel.Either
import io.golos.domain.entities.CountryEntity

interface SignUpCountryModel {
    suspend fun getCountries(): Either<List<CountryEntity>, Exception>

    suspend fun search(query: String): List<CountryEntity>
}