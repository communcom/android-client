package io.golos.data.repositories

import io.golos.domain.entities.CountryEntity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-09.
 */
interface CountriesProvider {
    suspend fun getAllCountries(): List<CountryEntity>
}