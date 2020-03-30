package io.golos.cyber_android.ui.screens.app_start.sign_up.confirm_password.view_model

import io.golos.cyber_android.ui.screens.app_start.sign_up.shared.messages_mapper.SignUpMessagesMapper
import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.model.SignUpCreatePasswordModel
import io.golos.cyber_android.ui.screens.app_start.sign_up.create_password.view_model.SignUpCreatePasswordViewModel
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.sign_up.SignUpState
import io.golos.use_cases.sign_up.core.SignUpCoreView
import javax.inject.Inject

class SignUpConfirmPasswordViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: SignUpCreatePasswordModel,
    signUpCore: SignUpCoreView,
    signUpMessagesMapper: SignUpMessagesMapper
) : SignUpCreatePasswordViewModel(
    dispatchersProvider,
    model,
    signUpCore,
    signUpMessagesMapper
) {
    override val isBackButtonVisible = true

    override fun onBackButtonClick() {
        signUpCore.moveToStateDirect(SignUpState.ENTERING_PASSWORD)
        _command.value = NavigateBackwardCommand()
    }

    override fun getInitPassword(): String = ""
}