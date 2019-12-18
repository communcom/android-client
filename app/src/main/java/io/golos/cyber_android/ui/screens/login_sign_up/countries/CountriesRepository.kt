package io.golos.cyber_android.ui.screens.login_sign_up.countries

import io.golos.domain.dto.CountryEntity

interface CountriesRepository {
    fun getCountries(): List<CountryEntity>

    fun search(query: String): List<CountryEntity>

    fun getCurrentCountry(): CountryEntity?
}