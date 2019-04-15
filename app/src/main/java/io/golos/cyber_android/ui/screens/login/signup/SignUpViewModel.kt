package io.golos.cyber_android.ui.screens.login.signup

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber4j.utils.toCyberName
import io.golos.cyber_android.utils.Event
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.interactors.model.*
import io.golos.domain.interactors.reg.SignOnUseCase
import io.golos.domain.map
import io.golos.domain.model.QueryResult

/**
 * Shared [ViewModel] for sign up process
 */
class SignUpViewModel(private val signOnUseCase: SignOnUseCase) : ViewModel() {

    /**
     * [LiveData] for current user registration state (see [UserRegistrationStateModel])
     */
    val stateLiveData = signOnUseCase.getAsLiveData.asEvent()

    /**
     * [LiveData] for generated user keys
     */
    val keysLiveData = signOnUseCase.getLastRegisteredUser

    val updatingStateLiveData = signOnUseCase.getUpdatingState.asEvent()

    /**
     * Provide update states for one subtype of [NextRegistrationStepRequestModel]
     */
    inline fun <reified T : NextRegistrationStepRequestModel> getUpdatingStateForStep() =
        updatingStateLiveData.map(Function<Event<QueryResult<NextRegistrationStepRequestModel>>, QueryResult<NextRegistrationStepRequestModel>?> {
            return@Function it?.getIf { this?.originalQuery is T }
        })

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
        signOnUseCase.makeRegistrationStep(SendSmsForVerificationRequestModel(currentPhone))
    }

    /**
     * Verifies sms code
     */
    fun verifyCode(code: String) {
        signOnUseCase.makeRegistrationStep(
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
        signOnUseCase.makeRegistrationStep(
            SetUserNameRequestModel(currentPhone, name.toCyberName())
        )
    }

    /**
     * Writes user into blockchain
     */
    fun writeToBlockchain() {
        signOnUseCase.makeRegistrationStep(
            WriteUserToBlockChainRequestModel(currentPhone, currentName.toCyberName())
        )
    }

    /**
     * Resends sms code
     */
    fun resendCode() {
        signOnUseCase.makeRegistrationStep(
            ResendSmsVerificationCodeModel(currentPhone)
        )
    }

    /**
     * Requests update of the user registration state
     */
    fun updateRegisterState(phone: String) {
        currentPhone = getNormalizedPhone(phone)
        signOnUseCase.makeRegistrationStep(
            GetUserRegistrationStepRequestModel(currentPhone)
        )
    }

    private fun getNormalizedPhone(phone: String) = "+${phone.trim().replace("\\D+".toRegex(), "")}"

    init {
        signOnUseCase.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        signOnUseCase.unsubscribe()
    }
}