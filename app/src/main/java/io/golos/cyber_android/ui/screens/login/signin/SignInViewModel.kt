package io.golos.cyber_android.ui.screens.login.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.entities.CyberUser
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.map
import io.golos.domain.requestmodel.AuthRequestModel
import io.golos.domain.requestmodel.QueryResult

/**
 * [ViewModel] for Sign In process. Provides live data for input validation result,
 * loading and error states
 */
class SignInViewModel(private val signInUseCase: SignInUseCase) : ViewModel() {

    private val validationResultLiveData = MutableLiveData<Boolean>(false)

    /**
     * [LiveData] that indicates validness of the credentials in [login] and [key] fields
     */
    val getValidationResultLiveData = validationResultLiveData as LiveData<Boolean>

    /**
     * [LiveData] that indicates if loading is in progress
     */
    val loadingLiveData = signInUseCase.getLogInStates.map {
        it?.get(currentUser) is QueryResult.Loading
    }.asEvent()

    /**
     * [LiveData] that indicates if there was error in sign in process
     */
    val errorLiveData = signInUseCase.getLogInStates.map {
        it?.get(currentUser) is QueryResult.Error
    }.asEvent()

    /**
     * [LiveData] that indicates current state of auth process
     */
    val authStateLiveData = signInUseCase.getAsLiveData.asEvent()

    private var login: String = ""
    private var key: String = ""
    private var currentUser: CyberUser? = null

    init {
        signInUseCase.subscribe()
    }

    fun onLoginInput(login: String) {
        this.login = login
        validate(login, this.key)
    }

    fun onKeyInput(key: String) {
        this.key = key
        validate(this.login, key)
    }

    fun signIn() {
        currentUser = CyberUser(login)
        signInUseCase.authWithCredentials(
            AuthRequestModel(
                currentUser!!,
                key
            )
        )
    }

    private fun validate(login: String, key: String) {
        val isValid = login.length > 3 && key.length > 3
        validationResultLiveData.postValue(isValid)
    }

    override fun onCleared() {
        super.onCleared()
        signInUseCase.unsubscribe()
    }
}