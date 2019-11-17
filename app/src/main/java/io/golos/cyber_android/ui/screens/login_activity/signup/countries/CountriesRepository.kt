package io.golos.cyber_android.ui.screens.login_activity.signup.countries

import io.golos.domain.dto.CountryEntity

interface CountriesRepository {
    fun getCountries(): List<CountryEntity>

    fun search(query: String): List<CountryEntity>

    fun getCurrentCountry(): CountryEntity?
}