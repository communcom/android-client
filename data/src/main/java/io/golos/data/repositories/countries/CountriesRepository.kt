package io.golos.data.repositories.countries

import io.golos.domain.entities.CountryEntity

interface CountriesRepository {
    fun getCountries(): List<CountryEntity>

    fun search(query: String): List<CountryEntity>

    fun getCurrentCountry(): CountryEntity?
}