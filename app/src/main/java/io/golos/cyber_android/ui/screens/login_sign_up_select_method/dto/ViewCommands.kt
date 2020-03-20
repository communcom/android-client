package io.golos.cyber_android.ui.screens.login_sign_up_select_method.dto

import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.SignUpIdentityDomain

data class NavigateToUserNameStepCommand(val identity: SignUpIdentityDomain): ViewCommand