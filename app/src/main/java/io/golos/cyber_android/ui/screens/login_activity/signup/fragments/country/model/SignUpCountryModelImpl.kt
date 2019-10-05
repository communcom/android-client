package io.golos.cyber_android.ui.screens.login_activity.signup.fragments.country.model

import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.application.App
import io.golos.data.repositories.countries.CountriesRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.CountryEntity
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignUpCountryModelImpl
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val countriesRepository: CountriesRepository
) : SignUpCountryModel {

    override suspend fun getCountries(): Either<List<CountryEntity>, Exception> =
        withContext(dispatchersProvider.ioDispatcher) {
            try {
                Either.Success<List<CountryEntity>, Exception>(countriesRepository.getCountries())
            } catch (ex: Exception) {
                App.logger.log(ex)
                Either.Failure<List<CountryEntity>, Exception>(ex)
            }
        }

    override suspend fun search(query: String): List<CountryEntity> =
        withContext(dispatchersProvider.calculationsDispatcher) {
            try {
                countriesRepository.search(query)
            } catch(ex: Exception) {
                App.logger.log(ex)
                countriesRepository.getCountries()
            }
        }
}