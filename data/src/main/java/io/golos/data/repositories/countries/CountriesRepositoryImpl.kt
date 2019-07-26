package io.golos.data.repositories.countries

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.golos.domain.AppResourcesProvider
import io.golos.domain.DeviceInfoService
import io.golos.domain.entities.CountryEntity
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-09.
 */
class CountriesRepositoryImpl
@Inject
constructor(
    private val appResourcesProvider: AppResourcesProvider,
    private val moshi: Moshi,
    private val deviceInfoService: DeviceInfoService
) : CountriesRepository {

    private lateinit var countries: List<CountryEntity>

    override fun getCountries(): List<CountryEntity> {
        val countriesRaw = String(appResourcesProvider.getCountries().readBytes())

        val countries = moshi.adapter<List<CountryEntity>>(
            Types.newParameterizedType(
                List::class.java,
                CountryEntity::class.java
            )
        ).fromJson(countriesRaw)!!

        this.countries = countries

        return countries
    }

    override fun search(query: String): List<CountryEntity> {
        if(!::countries.isInitialized) {
            getCountries()
        }

        if(query.isBlank() || query.isEmpty()) {
            return countries
        }

        val phoneCode = query.toIntOrNull()

        if(phoneCode != null) {
            return countries.filter { it.countryPhoneCode.toString().startsWith(query) }
        } else {
            val upperQuery = query.toUpperCase()
            return countries.filter { it.countryName.toUpperCase().startsWith(upperQuery) }
        }
    }

    override fun getCurrentCountry(): CountryEntity? {
        if(!::countries.isInitialized) {
            getCountries()
        }

        return deviceInfoService.getCountryCode()
            ?.let {
                val code = it.toUpperCase()
                countries.firstOrNull { it.countryCode == code }
            }
    }
}