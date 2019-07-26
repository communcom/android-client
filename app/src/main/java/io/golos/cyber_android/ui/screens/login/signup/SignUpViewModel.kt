package io.golos.cyber_android.ui.screens.login.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.utils.asEvent
import io.golos.data.repositories.countries.CountriesRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.CountryEntity
import io.golos.domain.interactors.model.*
import io.golos.domain.interactors.reg.SignUpUseCase
import io.golos.domain.map
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Shared [ViewModel] for sign up process
 */
class SignUpViewModel
@Inject
constructor(
    private val signUpUseCase: SignUpUseCase,
    private val countriesRepository: CountriesRepository,
    private val dispatchersProvider: DispatchersProvider
) : ViewModel(), CoroutineScope {

    private val scopeJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    /**
     * [LiveData] for current user registration state (see [UserRegistrationStateModel])
     */
    val stateLiveData = signUpUseCase.getAsLiveData.asEvent()

    /**
     * [LiveData] for generated user keys
     */
    val lastRegisteredUser = signUpUseCase.getLastRegisteredUser

    val updatingStateLiveData = signUpUseCase.getUpdatingState.asEvent()

    /**
     * Provide update states for one subtype of [NextRegistrationStepRequestModel]
     */
    inline fun <reified T : NextRegistrationStepRequestModel> getUpdatingStateForStep() =
        updatingStateLiveData.map {
            it?.getIf { this?.originalQuery is T }
        }

    private val selectedCountryLiveData = MutableLiveData<CountryEntity?>(null)

    private val selectedPhoneLiveData = MutableLiveData<String>("")


    /**
     * [LiveData] for country that was selected for phone number
     */
    val getSelectedCountryLiveData = selectedCountryLiveData as LiveData<CountryEntity?>

    /**
     * [LiveData] for country that was selected for phone number
     */
    val getSelectedPhoneLiveData = selectedPhoneLiveData as LiveData<String>

    /**
     * Sets [CountryModel] for this ViewModel
     */
    fun onCountrySelected(countryModel: CountryEntity) = selectedCountryLiveData.postValue(countryModel)


    private var currentPhone = ""
    private var currentName = ""

    /**
     * Sends first sms code
     */
    fun sendCodeOn(phone: String) {
        selectedPhoneLiveData.postValue(phone)

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
        selectedPhoneLiveData.postValue(phone)

        currentPhone = getNormalizedPhone(phone)
        signUpUseCase.makeRegistrationStep(
            GetUserRegistrationStepRequestModel(currentPhone)
        )
    }

    fun initSelectedCountry() {
        if(selectedCountryLiveData.value != null) {
            return
        }

        launch {
            selectedCountryLiveData.value = withContext(dispatchersProvider.ioDispatcher) {
                try {
                    countriesRepository.getCurrentCountry()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    null
                }
            }
        }
    }

    private fun getNormalizedPhone(phone: String) = "+${phone.trim().replace("\\D+".toRegex(), "")}"

    init {
        signUpUseCase.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        signUpUseCase.unsubscribe()
        scopeJob.takeIf { it.isActive }?.cancel()
    }
}