package io.golos.domain.dto

import io.golos.domain.Entity

data class CountryEntity(
    val countryCode: String,
    val name: String,
    val code: Int,
    val available: Boolean,
    val emoji: String
) : Entity

data class CountriesList(val countries: List<CountryEntity>) : List<CountryEntity> by countries, Entity