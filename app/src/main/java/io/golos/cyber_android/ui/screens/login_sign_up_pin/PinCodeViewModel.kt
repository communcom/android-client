package io.golos.cyber_android.ui.screens.login_sign_up_pin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.shared.mvvm.SingleLiveData
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToMainScreenCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.login_sign_up_pin.view_commands.NavigateToFingerprintCommand
import io.golos.cyber_android.ui.screens.login_sign_up_pin.view_commands.NavigateToKeysCommand
import io.golos.cyber_android.ui.screens.login_sign_up_pin.view_state_dto.CodeState
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.AuthType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.UnsupportedOperationException
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class PinCodeViewModel
@Inject
constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val model: PinCodeModel
) : ViewModel(), CoroutineScope {

    private val scopeJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    val isInExtendedMode = MutableLiveData(false)

    val codeState = MutableLiveData(
        CodeState(
            isPrimaryCodeActive = true,
            isRepeatedCodeActive = false,
            resetNeeded = false
        )
    )

    val command: SingleLiveData<ViewCommand> = SingleLiveData()

    fun onPrimaryCodeUpdated(code: String?) {
        model.updatePrimaryCode(code)

        if(model.isPrimaryCodeCompleted()) {
            isInExtendedMode.value = true
            codeState.value = CodeState(
                isPrimaryCodeActive = false,
                isRepeatedCodeActive = true,
                resetNeeded = false
            )
        }
    }

    fun onRepeatedCodeUpdated(code: String?) {
        model.updateRepeatedCode(code)

        if(model.isRepeatedCodeCompleted()) {
            if(model.validate()) {
                codeState.value = CodeState(
                    isPrimaryCodeActive = false,
                    isRepeatedCodeActive = true,
                    resetNeeded = false
                )

                launch {
                    if(model.saveCode()) {
                        command.value = if(model.isFingerprintAuthenticationPossible) {
                            NavigateToFingerprintCommand()
                        } else {
                            when(model.getAuthType()) {
                                AuthType.SIGN_IN -> NavigateToMainScreenCommand()
                                AuthType.SIGN_UP -> NavigateToKeysCommand()
                                else -> throw UnsupportedOperationException("This type is not supported")
                            }
                        }
                    } else {
                        command.value =
                            ShowMessageResCommand(R.string.common_general_error)
                    }
                }
            } else {
                model.updatePrimaryCode(null)
                model.updateRepeatedCode(null)

                command.value = ShowMessageResCommand(R.string.codes_not_match)
                codeState.value = CodeState(
                    isPrimaryCodeActive = true,
                    isRepeatedCodeActive = false,
                    resetNeeded = true
                )
                isInExtendedMode.value = false
            }
        }
    }

    fun onClearButtonClick() {
        codeState.value = CodeState(
            isPrimaryCodeActive = true,
            isRepeatedCodeActive = false,
            resetNeeded = true
        )
        isInExtendedMode.value = false
    }

    override fun onCleared() {
        scopeJob.takeIf { it.isActive }?.cancel()
    }
}