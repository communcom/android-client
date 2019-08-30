package io.golos.cyber_android.ui.screens.login_activity.signin.qr_code

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber4j.sharedmodel.Either
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.SingleLiveData
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.login_activity.signin.qr_code.detector.QrCodeDecrypted
import io.golos.cyber_android.ui.screens.login_activity.signin.qr_code.keys_extractor.QrCodeKeysExtractor
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.CyberUser
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.extensions.map
import io.golos.domain.requestmodel.QueryResult
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class QrCodeSignInViewModel
@Inject
constructor(
    private val signInUseCase: SignInUseCase,
    private val userKeysExtractor: QrCodeKeysExtractor,
    private val dispatchersProvider: DispatchersProvider
): ViewModel(), CoroutineScope {

    private val scopeJob: Job = SupervisorJob()

    private var currentUser: CyberUser? = null

    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    val command: SingleLiveData<ViewCommand> = SingleLiveData()

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

    init {
        signInUseCase.subscribe()
    }

    fun onCodeReceived(code: QrCodeDecrypted) {
        currentUser = CyberUser(code.userName)

        launch {
            val authRequest = withContext(dispatchersProvider.ioDispatcher) {
                userKeysExtractor.process(code)
            }

            if(authRequest is Either.Success) {
                signInUseCase.authWithCredentials(authRequest.value)
            } else {
                command.value = ShowMessageCommand(R.string.common_general_error)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        signInUseCase.unsubscribe()
        scopeJob.takeIf { it.isActive }?.cancel()
    }
}