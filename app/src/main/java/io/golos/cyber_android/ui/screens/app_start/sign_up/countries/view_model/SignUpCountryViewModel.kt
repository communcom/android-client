package io.golos.cyber_android.ui.screens.app_start.sign_up.countries.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.app_start.sign_up.countries.model.SignUpCountryModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.data_pass.SignUpFragmentsDataPass
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.analytics.AnalyticsFacade
import io.golos.domain.dto.CountryDomain
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpCountryViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: SignUpCountryModel,
    private val dataPass: SignUpFragmentsDataPass,
    private val analyticsFacade: AnalyticsFacade
) : ViewModelBase<SignUpCountryModel>(dispatchersProvider, model) {

    private val _countries = MutableLiveData<List<CountryDomain>>(listOf())
    val countries: LiveData<List<CountryDomain>> get() = _countries

    init {
        launch {
            analyticsFacade.openScreen111()

            _command.value = SetLoadingVisibilityCommand(true)

            try {
                _countries.value = model.getCountries()
            } catch (ex: Exception) {
                _command.value = ShowMessageResCommand(R.string.common_general_error )
            } finally {
                _command.value = SetLoadingVisibilityCommand(false)
            }
        }
    }

    fun makeSearch(query: String) {
        launch {
            _countries.value = if(query.isBlank()) {
                model.getCountries()
            } else {
                model.search(query)
            }
        }
    }

    fun onCountrySelected(country: CountryDomain) {
        analyticsFacade.countrySelected(country.available, country.code.toString())
        if(country.available) {
            dataPass.putSelectedCountry(country)
            _command.value = NavigateBackwardCommand()
        } else {
            _command.value = ShowMessageResCommand(R.string.not_support_region)
        }
    }
}