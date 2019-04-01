package io.golos.cyber_android.ui.screens.login.signin

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.entities.CyberUser
import io.golos.domain.interactors.model.UserAuthState
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.map
import io.golos.domain.model.AuthRequestModel
import io.golos.domain.model.QueryResult

/**
 * [ViewModel] for Sign In process. Provides live data for input validation result,
 * loading and error states
 */
class SignInViewModel(private val signInUseCase: SignInUseCase): ViewModel() {

    /**
     * [LiveData] that indicates validness of the credentials in [login] and [key] fields
     */
    val validationResultLiveData = MutableLiveData<Boolean>(false)

    /**
     * [LiveData] that indicates if loading is in progress
     */
    val loadingLiveData = signInUseCase.getLogInStates.map(Function<Map<CyberUser, QueryResult<AuthRequestModel>>, Boolean> {
        it[currentUser] is QueryResult.Loading
    }).asEvent()

    /**
     * [LiveData] that indicates if there was error in sign in process
     */
    val errorLiveData = signInUseCase.getLogInStates.map(Function<Map<CyberUser, QueryResult<AuthRequestModel>>, Boolean> {
        it[currentUser] is QueryResult.Error
    }).asEvent()

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