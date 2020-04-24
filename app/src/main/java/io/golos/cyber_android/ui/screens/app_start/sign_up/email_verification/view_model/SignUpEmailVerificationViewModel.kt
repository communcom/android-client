package io.golos.cyber_android.ui.screens.app_start.sign_up.email_verification.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.screens.app_start.sign_up.email_verification.model.SignUpEmailVerificationModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone.dto.NavigateToSelectSignUpMethodCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.phone_verification.dto.NavigateToUserNameCommand
import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.messages_mapper.SignUpMessagesMapper
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
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

class SignUpEmailVerificationViewModel
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    model: SignUpEmailVerificationModel,
    private val singUpCore: SignUpCoreView,
    private val signUpMessagesMapper: SignUpMessagesMapper,
    private val analyticsFacade: AnalyticsFacade
) : ViewModelBase<SignUpEmailVerificationModel>(dispatchersProvider, model) {

    private val resendTimeInSeconds = 59

    private val _nextButtonEnabled = MutableLiveData<Boolean>(false)
    val nextButtonEnabled: LiveData<Boolean> = _nextButtonEnabled

    private val _resendText = MutableLiveData<String>("")
    val resendText: LiveData<String> = _resendText

    val code = MutableLiveData<String>()

    val isBackButtonVisible = false

    private var timerJob: Job? = null

    init {
//        analyticsFacade.openScreen113()

        code.observeForever {
            _nextButtonEnabled.value = model.isCodeValid(it)
        }

        restartResendTimer()

        launch {
            singUpCore.commands.collect { processSignUnCommand(it) }
        }
    }

    fun onNextClick() {
//        analyticsFacade.smsCodeEntered(SmsCodeEntered.RIGHT)
        singUpCore.process(EmailVerificationCodeEntered(code.value!!))
    }

    fun onResendClick() {
        timerJob?.let {
            if(!it.isActive) {
                code.value = ""

                singUpCore.process(EmailVerificationCodeResend())
            }
        }
    }

    private fun processSignUnCommand(command: SignUpCommand) =
        when (command) {
            is ShowLoading -> _command.value = SetLoadingVisibilityCommand(true)
            is HideLoading -> _command.value = SetLoadingVisibilityCommand(false)
            is NavigateToUserName -> _command.value = NavigateToUserNameCommand()
            is ShowError -> {
                //analyticsFacade.smsCodeEntered(SmsCodeEntered.ERROR)
                _command.value = ShowMessageTextCommand(signUpMessagesMapper.getMessage(command.errorCode))
                code.value = ""
            }
            is ShowMessage -> _command.value = ShowMessageTextCommand(signUpMessagesMapper.getMessage(command.messageCode), false)
            is NavigateToSelectMethod -> _command.value = NavigateToSelectSignUpMethodCommand()

            is EmailVerificationCodeResendCompleted -> {
                //analyticsFacade.smsCodeEntered(SmsCodeEntered.RESEND)
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