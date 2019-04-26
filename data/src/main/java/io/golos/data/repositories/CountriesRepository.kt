package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.domain.DispatchersProvider
import io.golos.domain.Logger
import io.golos.domain.Repository
import io.golos.domain.entities.CountriesList
import io.golos.domain.requestmodel.CountriesRequest
import io.golos.domain.requestmodel.Identifiable
import io.golos.domain.requestmodel.QueryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-09.
 */
class CountriesRepository(
    private val dispatchersProvider: DispatchersProvider,
    private val resourceSupplier: CountriesProvider,
    private val logger: Logger
) : Repository<CountriesList, CountriesRequest> {

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private val countries = MutableLiveData<CountriesList>()

    private val updatingStateState =
        MutableLiveData<Map<Identifiable.Id, QueryResult<CountriesRequest>>>()

    private val countriesRequest = CountriesRequest()

    init {
        readCountriesFromResources()
    }

    private fun readCountriesFromResources() {
        repositoryScope.launch {
            updatingStateState.value = mapOf(allDataRequest.id to QueryResult.Loading(allDataRequest))
            try {
                val countriesList = withContext(dispatchersProvider.workDispatcher) {
                    resourceSupplier.getAllCountries()
                }.let { CountriesList(it) }

                countries.value = countriesList
                updatingStateState.value = mapOf(allDataRequest.id to QueryResult.Success(allDataRequest))

            } catch (e: Exception) {
                logger(e)
                updatingStateState.value = mapOf(allDataRequest.id to QueryResult.Error(e, allDataRequest))
            }
        }
    }


    override val allDataRequest: CountriesRequest
        get() = countriesRequest

    override fun getAsLiveData(params: CountriesRequest): LiveData<CountriesList> {
        return countries
    }

    override fun makeAction(params: CountriesRequest) {
        readCountriesFromResources()
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<CountriesRequest>>>
        get() = updatingStateState
}