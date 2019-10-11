package io.golos.cyber_android.ui.screens.login_activity.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.login_activity.signup.fragments.UserNameValidator
import io.golos.cyber_android.utils.asEvent
import io.golos.data.repositories.countries.CountriesRepository
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.scopes.ActivityScope
import io.golos.domain.entities.CountryEntity
import io.golos.domain.interactors.model.*
import io.golos.domain.interactors.reg.SignUpUseCase
import io.golos.domain.extensions.map
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Shared [ViewModel] for sign up process
 */
@ActivityScope
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

    private val selectedPhoneLiveData = MutableLiveData("")

    private val validateUserNameErrorLiveData = MutableLiveData<Int>()

    private val validateUserNameSuccessLiveData = MutableLiveData<Any>()

    /**
     * [LiveData] for country that was selected for phone number
     */
    val getSelectedCountryLiveData = selectedCountryLiveData as LiveData<CountryEntity?>

    /**
     * [LiveData] for country that was selected for phone number
     */
    val getSelectedPhoneLiveData = selectedPhoneLiveData as LiveData<String>

    val getValidateUserNameErrorLivaData = validateUserNameErrorLiveData as LiveData<Int>

    val getValidateUserNameSuccessLiveData = validateUserNameSuccessLiveData as LiveData<Any>

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
        launch {
            signUpUseCase.makeRegistrationStep(SendSmsForVerificationRequestModel(currentPhone))
        }
    }

    /**
     * Verifies sms code
     */
    fun verifyCode(code: String) {
        launch {
            signUpUseCase.makeRegistrationStep(
                SendVerificationCodeRequestModel(
                    currentPhone,
                    Integer.parseInt(code)
                )
            )
        }
    }

    /**
     * Sends username
     */
    fun sendName(name: String) {
        currentName = name
        launch {
            signUpUseCase.makeRegistrationStep(
                SetUserNameRequestModel(currentPhone, name)
            )
        }
    }

    /**
     * Writes user into blockchain
     */
    fun writeToBlockchain() {
        launch {
            signUpUseCase.makeRegistrationStep(
                WriteUserToBlockChainRequestModel(currentPhone, currentName)
            )
        }
    }

    /**
     * Resends sms code
     */
    fun resendCode() {
        launch {
            signUpUseCase.makeRegistrationStep(
                ResendSmsVerificationCodeModel(currentPhone)
            )
        }
    }

    /**
     * Requests update of the user registration state
     */
    fun updateRegisterState(phone: String = currentPhone) {
        selectedPhoneLiveData.postValue(phone)

        currentPhone = getNormalizedPhone(phone)
        launch {
            signUpUseCase.makeRegistrationStep(
                GetUserRegistrationStepRequestModel(currentPhone)
            )
        }
    }

    /**
     * Validate user name by rules application.
     * If user name correct, then view model
     *
     * @param userName user name
     */
    fun validateUserName(userName: String) {
        val userNameValidator = UserNameValidator()
        if(userNameValidator.isValid(userName)){
            validateUserNameSuccessLiveData.postValue(Any())
        } else{
            validateUserNameErrorLiveData.postValue(userNameValidator.getValidateErrorMessage())
        }
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
                    App.logger.log(ex)
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
        signUpUseCase.unsubscribe()
        scopeJob.takeIf { it.isActive }?.cancel()
        super.onCleared()
    }
}