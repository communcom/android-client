package io.golos.cyber_android.ui.screens.login_sign_up

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.ui.screens.login_shared.fragments_data_pass.LoginActivityFragmentsDataPass
import io.golos.cyber_android.ui.screens.login_shared.validators.user_name.validator.UserNameValidationResult
import io.golos.cyber_android.ui.screens.login_shared.validators.user_name.validator.UserNameValidator
import io.golos.cyber_android.ui.screens.login_shared.validators.user_name.vizualizer.UserNameValidationVisualizer
import io.golos.cyber_android.ui.shared.utils.asEvent
import io.golos.domain.DispatchersProvider
import io.golos.domain.dependency_injection.scopes.ActivityScope
import io.golos.domain.dto.CountryDomain
import io.golos.domain.extensions.map
import io.golos.domain.use_cases.model.*
import io.golos.domain.use_cases.reg.SignUpUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
    private val dispatchersProvider: DispatchersProvider,
    private val dataPass: LoginActivityFragmentsDataPass,
    private val userNameValidator: UserNameValidator,
    private val userNameValidationVisualizer: UserNameValidationVisualizer
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

    private val selectedCountryLiveData = MutableLiveData<CountryDomain?>(null)

    private val selectedPhoneLiveData = MutableLiveData("")

    private val validateUserNameErrorLiveData = MutableLiveData<String>()

    private val validateUserNameSuccessLiveData = MutableLiveData<Any>()

    /**
     * [LiveData] for country that was selected for phone number
     */
    val getSelectedCountryLiveData = selectedCountryLiveData as LiveData<CountryDomain?>

    /**
     * [LiveData] for country that was selected for phone number
     */
    val getSelectedPhoneLiveData = selectedPhoneLiveData as LiveData<String>

    val getValidateUserNameErrorLivaData = validateUserNameErrorLiveData as LiveData<String>

    val getValidateUserNameSuccessLiveData = validateUserNameSuccessLiveData as LiveData<Any>

    /**
     * Sets [CountryModel] for this ViewModel
     */
    fun onCountrySelected(countryModel: CountryDomain) = selectedCountryLiveData.postValue(countryModel)


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
    fun writeToBlockchain(userName: String, userId: String) {
        launch {
            signUpUseCase.makeRegistrationStep(
                WriteUserToBlockChainRequestModel(currentPhone, currentName, userId)
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
        dataPass.putPhone(currentPhone)

        launch {
            signUpUseCase.userName = currentName
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
        val validationResult = userNameValidator.validate(userName)
        if(validationResult == UserNameValidationResult.SUCCESS) {
            currentName = userName
            validateUserNameSuccessLiveData.postValue(userName)
        } else {
            validateUserNameErrorLiveData.postValue(userNameValidationVisualizer.toSting(validationResult))
        }
    }

    fun resetCountrySelection() {
        selectedCountryLiveData.value = null
    }

    fun getNormalizedPhone(phone: String) = "+${phone.trim().replace("\\D+".toRegex(), "")}"

    init {
        signUpUseCase.subscribe()
    }

    override fun onCleared() {
        signUpUseCase.unsubscribe()
        scopeJob.takeIf { it.isActive }?.cancel()
        super.onCleared()
    }
}