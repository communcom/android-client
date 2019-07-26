package io.golos.cyber_android.ui.screens.login.signup.country.model

import io.golos.domain.entities.CountryEntity
import io.golos.sharedmodel.Either

interface SignUpCountryModel {
    suspend fun getCountries():Either<List<CountryEntity>, Exception>

    suspend fun search(query: String): List<CountryEntity>
}