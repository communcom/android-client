package io.golos.cyber_android.ui.screens.login_sign_up_create_password.view_model

import io.golos.cyber_android.ui.screens.login_sign_up_create_password.model.SignUpCreatePasswordModel
import io.golos.cyber_android.ui.shared.mvvm.viewModel.ViewModelBase
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class SignUpCreatePasswordViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: SignUpCreatePasswordModel
) : ViewModelBase<SignUpCreatePasswordModel>(dispatchersProvider, model) {
}