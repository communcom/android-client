package io.golos.cyber_android.ui.screens.login_activity.signup.countries

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.golos.cyber_android.R
import io.golos.domain.DeviceInfoProvider
import io.golos.domain.dto.CountryEntity
import javax.inject.Inject

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-09.
 */
class CountriesRepositoryImpl
@Inject
constructor(
    private val context: Context,
    private val moshi: Moshi,
    private val deviceInfoProvider: DeviceInfoProvider
) : CountriesRepository {

    private lateinit var countries: List<CountryEntity>

    override fun getCountries(): List<CountryEntity> {
        val countriesRaw = String(context.resources.openRawResource(R.raw.countries).readBytes())

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

        return deviceInfoProvider.getCountryCode()
            ?.let {
                val code = it.toUpperCase()
                countries.firstOrNull { it.countryCode == code }
            }
    }
}