package io.golos.cyber_android.ui.screens.login.signup.fingerprint

import androidx.lifecycle.ViewModel
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.mvvm.SingleLiveData
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.entities.AppUnlockWay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class FingerprintViewModel
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val model: FingerprintModel
) : ViewModel(), CoroutineScope {

    private val scopeJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    val command: SingleLiveData<ViewCommand> = SingleLiveData()

    fun onUnlockViaPinCodeClick() = saveUnlockWay(AppUnlockWay.PIN_CODE)

    fun onUnlockViaFingerprintClick()  = saveUnlockWay(AppUnlockWay.FINGERPRINT)

    override fun onCleared() {
        scopeJob.takeIf { it.isActive }?.cancel()
    }

    private fun saveUnlockWay(appUnlockWay: AppUnlockWay) {
        launch {
            if(model.saveAppUnlockWay(appUnlockWay)) {
                command.value = NavigateToKeysCommand()
            } else {
                command.value =
                    ShowMessageCommand(R.string.common_general_error)
            }
        }
    }
}