package io.golos.cyber_android.ui.screens.app_start.sign_up.select_method.dto

import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.SignUpIdentityDomain

data class NavigateToUserNameStepCommand(val identity: SignUpIdentityDomain): ViewCommand