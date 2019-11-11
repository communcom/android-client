package io.golos.cyber_android.ui.screens.login_activity.signin.user_name

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.commun4j.sharedmodel.Either
import io.golos.cyber_android.R
import io.golos.cyber_android.core.keys_backup.facade.BackupKeysFacade
import io.golos.cyber_android.ui.common.mvvm.SingleLiveData
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.login_activity.signin.user_name.keys_extractor.MasterPassKeysExtractor
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.CyberUser
import io.golos.domain.use_cases.sign.SignInUseCase
import io.golos.domain.extensions.map
import io.golos.domain.requestmodel.AuthRequestModel
import io.golos.domain.requestmodel.QueryResult
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * [ViewModel] for Sign In process. Provides live data for input validation result,
 * loading and error states
 */
class UserNameSignInViewModel
@Inject
constructor(
    private val signInUseCase: SignInUseCase,
    private val userKeysExtractor: MasterPassKeysExtractor,
    private val dispatchersProvider: DispatchersProvider,
    private val backupKeysFacade: BackupKeysFacade
) : ViewModel(), CoroutineScope {

    private val scopeJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    private val validationResultLiveData = MutableLiveData(false)

    val command: SingleLiveData<ViewCommand> = SingleLiveData()

    val restoreFromCloudButtonEnabled = MutableLiveData(false)

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

        if(login.isNotEmpty()) {
            launch {
                restoreFromCloudButtonEnabled.value = withContext(dispatchersProvider.ioDispatcher) {
                    backupKeysFacade.isStorageExists()
                }
            }
        } else {
            restoreFromCloudButtonEnabled.value = false
        }
    }

    fun onKeyInput(key: String) {
        this.key = key
        validate(this.login, key)
    }

    fun signIn() {
        currentUser = CyberUser(login)

        launch {
            val authRequest = withContext(dispatchersProvider.ioDispatcher) {
                userKeysExtractor.process(login, key)
            }

            if(authRequest is Either.Success<AuthRequestModel, Exception>) {
                signInUseCase.authWithCredentials(authRequest.value)
            } else {
                command.value = ShowMessageCommand(R.string.common_general_error)
            }
        }
    }

    fun onRestoreFromCloud() {
        launch {
            val key = withContext(dispatchersProvider.ioDispatcher) {
                backupKeysFacade.getKey(login)
            }

            if(key != null) {
                command.value = SetKeyValueViewCommand(key)
            } else {
                command.value = ShowMessageCommand(R.string.no_master_password)
            }
        }
    }

    private fun validate(login: String, key: String) {
        val isValid = login.length > 1 && key.length > 3
        validationResultLiveData.postValue(isValid)
    }

    override fun onCleared() {
        super.onCleared()
        signInUseCase.unsubscribe()
        scopeJob.takeIf { it.isActive }?.cancel()
    }
}