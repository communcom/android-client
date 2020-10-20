package io.golos.cyber_android.ui.shared.countries

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.golos.cyber_android.R
import io.golos.domain.DeviceInfoProvider
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.scopes.UIScope
import io.golos.domain.dto.CountryDomain
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@UIScope
class CountriesRepositoryImpl
@Inject
constructor(
    private val context: Context,
    private val moshi: Moshi,
    private val deviceInfoProvider: DeviceInfoProvider,
    private val dispatchersProvider: DispatchersProvider
) : CountriesRepository {

    private lateinit var countries: List<CountryEntity>

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getCountries(): List<CountryDomain> {
        if(::countries.isInitialized) {
            return countries.map { it.country }
        }

        return withContext(dispatchersProvider.calculationsDispatcher) {
            val countriesRaw = String(context.resources.openRawResource(R.raw.countries).readBytes())

            val rawCountries = moshi
                .adapter<List<CountryDomain>>(Types.newParameterizedType(List::class.java,CountryDomain::class.java))
                .fromJson(countriesRaw)!!
                .sortedWith(compareBy({ it.code }, { it.name }))

            countries = rawCountries.map { CountryEntity(it, it.name.toLowerCase(Locale.getDefault())) }

            rawCountries
        }
    }

    override fun search(query: String): List<CountryDomain> {
        val preparedQuery = query.toLowerCase(Locale.getDefault())

        return countries
            .filter { it.searchName.startsWith(preparedQuery) }
            .map { it.country }
    }

    override suspend fun getCurrentCountry(): CountryDomain? {
        if (!::countries.isInitialized) {
            getCountries()
        }

        return withContext(dispatchersProvider.calculationsDispatcher) {
            deviceInfoProvider.getCountryCode()
                ?.let {
                    val code = it.toUpperCase(Locale.getDefault())
                    countries.firstOrNull { it.country.countryCode == code }?.country
                }
            }
    }
}