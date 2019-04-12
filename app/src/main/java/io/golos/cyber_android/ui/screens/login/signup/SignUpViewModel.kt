package io.golos.cyber_android.ui.screens.login.signup

import android.util.Log
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.interactors.model.*
import io.golos.domain.interactors.reg.SignOnUseCase
import io.golos.domain.map
import io.golos.domain.model.QueryResult

/**
 * Shared [ViewModel] for sign up process
 */
class SignUpViewModel(private val signOnUseCase: SignOnUseCase) : ViewModel() {

    private val stateLiveData = signOnUseCase.getAsLiveData

    val updatingStateLiveData = signOnUseCase.getUpdatingState

    inline fun <reified T : NextRegistrationStepRequestModel> getUpdatingStateForStep() =
        updatingStateLiveData.map(Function<QueryResult<NextRegistrationStepRequestModel>, QueryResult<NextRegistrationStepRequestModel>?> {
            if (it.originalQuery is T)
                return@Function it
            else null
        }).asEvent()

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

    /**
     * Sends first sms code
     */
    fun sendSmsCodeOn(phone: String) {
        val normalizedPhone = "+${phone.replace("\\D+".toRegex(), "")}"
        currentPhone = normalizedPhone
        signOnUseCase.makeRegistrationStep(SendSmsForVerificationRequestModel(normalizedPhone))
    }

    fun verifySmsCode(code: String) {
        signOnUseCase.makeRegistrationStep(
            SendVerificationCodeRequestModel(
                currentPhone,
                Integer.parseInt(code)
            )
        )
    }

    init {
        signOnUseCase.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        signOnUseCase.unsubscribe()
    }
}