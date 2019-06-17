package io.golos.cyber_android.ui.screens.login.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.interactors.model.*
import io.golos.domain.interactors.reg.SignUpUseCase
import io.golos.domain.map

/**
 * Shared [ViewModel] for sign up process
 */
class SignUpViewModel(private val signUpUseCase: SignUpUseCase) : ViewModel() {

    /**
     * [LiveData] for current user registration state (see [UserRegistrationStateModel])
     */
    val stateLiveData = signUpUseCase.getAsLiveData.asEvent()

    /**
     * [LiveData] for generated user keys
     */
    val keysLiveData = signUpUseCase.getLastRegisteredUser

    val updatingStateLiveData = signUpUseCase.getUpdatingState.asEvent()

    /**
     * Provide update states for one subtype of [NextRegistrationStepRequestModel]
     */
    inline fun <reified T : NextRegistrationStepRequestModel> getUpdatingStateForStep() =
        updatingStateLiveData.map {
            it?.getIf { this?.originalQuery is T }
        }

    private val selectedCountryLiveData = MutableLiveData<CountryModel?>(null)

    /**
     * [LiveData] for country that was selected for phone number
     */
    val getSelectedCountryLiveData = selectedCountryLiveData as LiveData<CountryModel?>

    /**
     * Sets [CountryModel] for this ViewModel
     */
    fun onCountrySelected(countryModel: CountryModel) = selectedCountryLiveData.postValue(countryModel)


    private var currentPhone = ""
    private var currentName = ""

    /**
     * Sends first sms code
     */
    fun sendCodeOn(phone: String) {
        currentPhone = getNormalizedPhone(phone)
        signUpUseCase.makeRegistrationStep(SendSmsForVerificationRequestModel(currentPhone))
    }

    /**
     * Verifies sms code
     */
    fun verifyCode(code: String) {
        signUpUseCase.makeRegistrationStep(
            SendVerificationCodeRequestModel(
                currentPhone,
                Integer.parseInt(code)
            )
        )
    }

    /**
     * Sends username
     */
    fun sendName(name: String) {
        currentName = name
        signUpUseCase.makeRegistrationStep(
            SetUserNameRequestModel(currentPhone, name)
        )
    }

    /**
     * Writes user into blockchain
     */
    fun writeToBlockchain() {
        signUpUseCase.makeRegistrationStep(
            WriteUserToBlockChainRequestModel(currentPhone, currentName)
        )
    }

    /**
     * Resends sms code
     */
    fun resendCode() {
        signUpUseCase.makeRegistrationStep(
            ResendSmsVerificationCodeModel(currentPhone)
        )
    }

    /**
     * Requests update of the user registration state
     */
    fun updateRegisterState(phone: String = currentPhone) {
        currentPhone = getNormalizedPhone(phone)
        signUpUseCase.makeRegistrationStep(
            GetUserRegistrationStepRequestModel(currentPhone)
        )
    }

    private fun getNormalizedPhone(phone: String) = "+${phone.trim().replace("\\D+".toRegex(), "")}"

    init {
        signUpUseCase.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        signUpUseCase.unsubscribe()
    }
}