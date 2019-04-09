package io.golos.domain.interactors.reg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.golos.domain.DispatchersProvider
import io.golos.domain.Repository
import io.golos.domain.entities.CountriesList
import io.golos.domain.entities.CountryEntity
import io.golos.domain.interactors.UseCase
import io.golos.domain.interactors.model.CountriesListModel
import io.golos.domain.interactors.model.CountryModel
import io.golos.domain.map
import io.golos.domain.model.CountriesRequest
import io.golos.domain.model.QueryResult
import io.golos.domain.rules.EntityToModelMapper
import kotlinx.coroutines.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-09.
 */
class CountriesChooserUseCase(
    private val countriesRepository: Repository<CountriesList, CountriesRequest>,
    private val mapper: EntityToModelMapper<CountryEntity, CountryModel>,
    private val dispatchersProvider: DispatchersProvider
) : UseCase<CountriesListModel> {

    private val lastSearchResult = MutableLiveData<CountriesListModel>()
    private var allCountries = listOf<CountryEntity>()
    private val redinessLiveData = MutableLiveData<QueryResult<Unit>>()

    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    private var searchJob: Job? = null

    private val observer = Observer<Any> {}
    private val mediator = MediatorLiveData<Any>()

    override val getAsLiveData: LiveData<CountriesListModel>
        get() = lastSearchResult

    val useCaseRedinessState = redinessLiveData as LiveData<QueryResult<Unit>>

    override fun subscribe() {
        super.subscribe()
        mediator.observeForever(observer)
        mediator.addSource(countriesRepository.getAsLiveData(countriesRepository.allDataRequest)) {
            allCountries = it
        }
        mediator.addSource(countriesRepository.updateStates) {
            val first = it.values.firstOrNull()
            redinessLiveData.value = when (first) {
                null -> QueryResult.Loading(Unit)
                else -> first.map(Unit)
            }
        }
    }

    override fun unsubscribe() {
        super.unsubscribe()
        mediator.removeObserver(observer)
        mediator.removeSource(countriesRepository.getAsLiveData(countriesRepository.allDataRequest))
        mediator.removeSource(countriesRepository.updateStates)
    }

    fun makeSearch(countryCode: Int) {
        searchJob?.cancel()
        repositoryScope.launch {
            val searchCodeString = countryCode.toString()
            delay(30)
            lastSearchResult.value = withContext(dispatchersProvider.workDispatcher) {
                CountriesListModel(allCountries.filter {
                    it.countryPhoneCode.toString().startsWith(searchCodeString)
                }
                    .map { mapper(it) })
            }
        }
    }

    fun makeSearch(countryName: String) {
        searchJob?.cancel()
        repositoryScope.launch {
            delay(30)
            val normalizedName = countryName.toLowerCase()
            lastSearchResult.value = CountriesListModel(allCountries.filter {
                it.countryName.startsWith(normalizedName, true)
            }
                .map { mapper(it) })
        }
    }
}