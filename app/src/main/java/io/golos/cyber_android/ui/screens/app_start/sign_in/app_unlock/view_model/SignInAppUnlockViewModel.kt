package io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.app_start.sign_in.app_unlock.model.AppUnlockModel
import io.golos.domain.analytics.AnalyticsFacade
import io.golos.cyber_android.ui.shared.mvvm.SingleLiveData
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToInAppAuthScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.AppUnlockWay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

open class SignInAppUnlockViewModel
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    protected val model: AppUnlockModel,
    private val analyticsFacade: AnalyticsFacade
) : ViewModel(), CoroutineScope {

    private val scopeJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    protected val _command = SingleLiveData<ViewCommand>()
    val command: LiveData<ViewCommand>
        get() = _command

    init {
        analyticsFacade.openScreen116()
    }

    fun onUnlockViaPinCodeClick() {
        analyticsFacade.faceIDTouchIDActivated(false)
        saveUnlockWay(AppUnlockWay.PIN_CODE)
    }

    fun onUnlockViaFingerprintClick() {
        _command.value = NavigateToInAppAuthScreenCommand(false)
    }

    fun onFingerprintConfirmed() {
        analyticsFacade.faceIDTouchIDActivated(true)
        saveUnlockWay(AppUnlockWay.FINGERPRINT)
    }

    override fun onCleared() {
        scopeJob.takeIf { it.isActive }?.cancel()
    }

    protected open fun saveUnlockWay(appUnlockWay: AppUnlockWay) {
        launch {
            try {
                model.saveAppUnlockWay(appUnlockWay)
                model.removeSignUpSnapshot()
                _command.value = NavigateToMainScreenCommand()
            } catch (ex: Exception) {
                _command.value = ShowMessageResCommand(R.string.common_general_error)
            }
        }
    }
}