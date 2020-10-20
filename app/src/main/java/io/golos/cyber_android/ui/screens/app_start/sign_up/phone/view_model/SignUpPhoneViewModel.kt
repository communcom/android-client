package io.golos.cyber_android.ui.screens.app_start.sign_up.phone.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToCountriesListCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToPhoneVerificationCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToSelectSignUpMethodCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.ShowCaptchaCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.model.SignUpPhoneModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.data_pass.SignUpFragmentsDataPass
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.messages_mapper.SignUpMessagesMapper
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.HideKeyboardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.analytics.AnalyticsFacade
import io.golos.domain.dto.CountryDomain
import io.golos.use_cases.sign_up.core.SignUpCoreView
import io.golos.use_cases.sign_up.core.data_structs.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpPhoneViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: SignUpPhoneModel,
    private val singUpCore: SignUpCoreView,
    private val signUpMessagesMapper: SignUpMessagesMapper,
    private val dataPass: SignUpFragmentsDataPass,
    analyticsFacade: AnalyticsFacade
) : ViewModelBase<SignUpPhoneModel>(dispatchersProvider, model) {

    private val _currentCountry = MutableLiveData<CountryDomain>()
    val currentCountry: LiveData<CountryDomain> = _currentCountry

    private val _phoneEnabled = MutableLiveData<Boolean>(false)
    val phoneEnabled: LiveData<Boolean> = _phoneEnabled

    val phone = MutableLiveData<String>("")

    private val _nextButtonEnabled = MutableLiveData<Boolean>(false)
    val nextButtonEnabled: LiveData<Boolean> = _nextButtonEnabled

    val isBackButtonVisible = false

    init {
        analyticsFacade.openScreen112()

        launch {
            singUpCore.commands.collect { processSignUnCommand(it) }
        }

        _currentCountry.observeForever {
            _phoneEnabled.value = true
        }

        phone.observeForever {
            model.isPhoneValid(it)
                .let { isValid ->
                    _nextButtonEnabled.value = isValid

                    if(isValid) {
                        analyticsFacade.phoneNumberEntered()
                    }
                }
        }

        launch {
            model.getCurrentCountry()?.let {
                if (it.available) {
                    _currentCountry.value = it
                }
            }
        }
    }

    fun onActive() {
        model.updateCurrentCountry(dataPass.getSelectedCountry())
            ?.let {
                _currentCountry.value = it
            }
    }

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }

    fun onCountyFieldClick() {
        _command.value = HideKeyboardCommand()
        _command.value = NavigateToCountriesListCommand()
    }

    fun onNextButtonClick() {
        _command.value = ShowCaptchaCommand()
    }

    fun onCaptchaReceived(captcha: String) {
        val phoneToProcess = phone.value!!
        val translatedPhone = StringBuilder("+")
        phoneToProcess.forEach {
            if(it.isDigit()) {
                translatedPhone.append(it)
            }
         }

        singUpCore.process(PhoneEntered(translatedPhone.toString(), captcha))
    }

    private fun processSignUnCommand(command: SignUpCommand) =
        when (command) {
            is ShowLoading -> _command.value = SetLoadingVisibilityCommand(true)
            is HideLoading -> _command.value = SetLoadingVisibilityCommand(false)
            is NavigateToPhoneVerification -> _command.value = NavigateToPhoneVerificationCommand()
            is ShowError -> _command.value = ShowMessageTextCommand(signUpMessagesMapper.getMessage(command.errorCode))
            is NavigateToSelectMethod -> _command.value = NavigateToSelectSignUpMethodCommand()
            else -> {}
        }
}