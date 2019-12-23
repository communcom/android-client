package io.golos.cyber_android.ui.screens.login_sign_up.fragments.country

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.SingleLiveData
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.login_sign_up.fragments.country.model.SignUpCountryModel
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CountryEntity
import io.golos.domain.extensions.fold
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SignUpCountryViewModel
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val model: SignUpCountryModel
) : ViewModel(), CoroutineScope {

    private var searchJob: Job? = null
    private val scopeJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    /**
     * List of countries
     */
    val countries = MutableLiveData<List<CountryEntity>>()

    val command: SingleLiveData<ViewCommand> = SingleLiveData()

    /**
     * Performs country search. Result can be listened by [countries]
     */
    fun makeSearch(query: String) {
        searchJob?.cancel()
        searchJob = launch {
            delay(220)
            countries.value = model.search(query)
        }
    }

    init {
        command.value = SetLoadingVisibilityCommand(true)
        launch {
            val modelCountries = model.getCountries()
            command.value = SetLoadingVisibilityCommand(false)

            modelCountries.fold({ countries.value = it }, { command.value = ShowMessageResCommand(R.string.common_general_error )})
        }
    }

    override fun onCleared() {
        super.onCleared()
        scopeJob.takeIf { it.isActive }?.cancel()
    }
}