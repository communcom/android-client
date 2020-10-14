package io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToSelectSignUpMethodCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.dto.ClearCodeCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.dto.NavigateToUserNameCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.model.SignUpPhoneVerificationModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.messages_mapper.SignUpMessagesMapper
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ShowMessageTextCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.analytics.AnalyticsFacade
import io.golos.domain.analytics.SmsCodeEntered
import io.golos.use_cases.sign_up.core.SignUpCoreView
import io.golos.use_cases.sign_up.core.data_structs.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpPhoneVerificationViewModel
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    model: SignUpPhoneVerificationModel,
    private val singUpCore: SignUpCoreView,
    private val signUpMessagesMapper: SignUpMessagesMapper,
    private val analyticsFacade: AnalyticsFacade
) : ViewModelBase<SignUpPhoneVerificationModel>(dispatchersProvider, model) {

    private val resendTimeInSeconds = 59

    private val _nextButtonEnabled = MutableLiveData<Boolean>(false)
    val nextButtonEnabled: LiveData<Boolean> = _nextButtonEnabled

    private val _resendText = MutableLiveData<String>("")
    val resendText: LiveData<String> = _resendText

    val isBackButtonVisible = false

    private var timerJob: Job? = null

    init {
        analyticsFacade.openScreen113()

        restartResendTimer()

        launch {
            singUpCore.commands.collect { processSignUnCommand(it) }
        }
    }

    fun onBackClick() {
        _command.value = NavigateBackwardCommand()
    }

    fun onCodeUpdated(code: String) {
        _nextButtonEnabled.value = model.updateCode(code)
    }

    fun onNextClick() {
        analyticsFacade.smsCodeEntered(SmsCodeEntered.RIGHT)
        singUpCore.process(PhoneVerificationCodeEntered(model.code.toInt()))
    }

    fun onResendClick() {
        timerJob?.let {
            if(!it.isActive) {
                model.updateCode("")
                _command.value = ClearCodeCommand()

                singUpCore.process(PhoneVerificationCodeResend())
            }
        }
    }

    private fun processSignUnCommand(command: SignUpCommand) =
        when (command) {
            is ShowLoading -> _command.value = SetLoadingVisibilityCommand(true)
            is HideLoading -> _command.value = SetLoadingVisibilityCommand(false)
            is NavigateToUserName -> _command.value = NavigateToUserNameCommand()
            is ShowError -> {
                analyticsFacade.smsCodeEntered(SmsCodeEntered.ERROR)
                _command.value = ShowMessageTextCommand(signUpMessagesMapper.getMessage(command.errorCode))
                _command.value = ClearCodeCommand()
            }
            is ShowMessage -> _command.value = ShowMessageTextCommand(signUpMessagesMapper.getMessage(command.messageCode), false)
            is NavigateToSelectMethod -> _command.value = NavigateToSelectSignUpMethodCommand()

            is PhoneVerificationCodeResendCompleted -> {
                analyticsFacade.smsCodeEntered(SmsCodeEntered.RESEND)
                restartResendTimer()
            }

            else -> {}
        }

    private fun restartResendTimer() {
        timerJob?.cancel()
        timerJob = launch {
            for (tick in resendTimeInSeconds downTo 0) {
                _resendText.value = String.format(appContext.getString(R.string.resend_verification_code_w_time_format), tick)
                delay(1_000)
            }
            _resendText.value = appContext.resources.getString(R.string.resend_verification_code)
        }
    }
}