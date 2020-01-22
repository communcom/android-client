package io.golos.cyber_android.ui.shared.countries

import io.golos.domain.dto.CountryDomain

interface CountriesRepository {
    suspend fun getCountries(): List<CountryDomain>

    fun search(query: String): List<CountryDomain>

    suspend fun getCurrentCountry(): CountryDomain?
}