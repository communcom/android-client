package io.golos.cyber_android.ui.screens.login_sign_in_qr_code.view_model

import io.golos.cyber_android.ui.common.mvvm.viewModel.ViewModelBase
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.model.SignInQrCodeModel
import io.golos.domain.DispatchersProvider
import javax.inject.Inject

class SignInQrCodeViewModel
@Inject
constructor(
    dispatchersProvider: DispatchersProvider,
    model: SignInQrCodeModel
) : ViewModelBase<SignInQrCodeModel>(dispatchersProvider, model) {

}