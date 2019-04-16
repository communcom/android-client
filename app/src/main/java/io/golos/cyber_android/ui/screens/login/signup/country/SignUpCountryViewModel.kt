package io.golos.cyber_android.ui.screens.login.signup.country

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.golos.domain.DispatchersProvider
import io.golos.domain.interactors.reg.CountriesChooserUseCase
import kotlinx.coroutines.*
import java.lang.NumberFormatException

class SignUpCountryViewModel(
    dispatchersProvider: DispatchersProvider,
    private val countriesChooserUseCase: CountriesChooserUseCase
) : ViewModel() {

    private val searchJobScope = CoroutineScope(dispatchersProvider.uiDispatcher)
    private var searchJob: Job? = null

    /**
     * [LiveData] for countries list
     */
    val countriesLiveData = countriesChooserUseCase.getAsLiveData

    /**
     * [LiveData] that represent readiness of the countries list
     */
    val readinessLiveData = countriesChooserUseCase.useCaseRedinessState

    /**
     * Performs country search. Result can be listened by [countriesLiveData]
     */
    fun makeSearch(query: String) {
        searchJob?.cancel()
        searchJob = searchJobScope.launch {
            delay(220)
            try {
                val countryCode = Integer.parseInt(query)
                if (countryCode > 0)
                    countriesChooserUseCase.makeSearch(countryCode)
                else throw NumberFormatException("country code is less than 0")
            } catch (e: NumberFormatException) {
                countriesChooserUseCase.makeSearch(query)
            }
        }
    }

    init {
        countriesChooserUseCase.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        countriesChooserUseCase.unsubscribe()
    }
}