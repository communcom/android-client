package io.golos.cyber_android.ui.screens.app_start.sign_in.pin.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.dto.CodeState
import io.golos.cyber_android.ui.screens.app_start.sign_in.pin.model.PinCodeModel
import io.golos.cyber_android.ui.shared.mvvm.SingleLiveData
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageResCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class PinCodeViewModelBase
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

    protected val _command = SingleLiveData<ViewCommand>()
    val command: LiveData<ViewCommand>
        get() = _command

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
                    saveCode()
                }
            } else {
                model.updatePrimaryCode(null)
                model.updateRepeatedCode(null)

                _command.value = ShowMessageResCommand(R.string.codes_not_match)
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

    protected abstract suspend fun saveCode()
}