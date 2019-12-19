package io.golos.cyber_android.ui.screens.login_sign_in_qr_code.view

import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentSignInQrCodeBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.di.SignInQrCodeFragmentComponent
import io.golos.cyber_android.ui.screens.login_sign_in_qr_code.view_model.SignInQrCodeViewModel

class SignInQrCodeFragment : FragmentBaseMVVM<FragmentSignInQrCodeBinding, SignInQrCodeViewModel>() {
    companion object {
        fun newInstance() = SignInQrCodeFragment()
    }

    override fun provideViewModelType(): Class<SignInQrCodeViewModel> = SignInQrCodeViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_sign_in_qr_code

    override fun inject() = App.injections.get<SignInQrCodeFragmentComponent>().inject(this)

    override fun releaseInjection() {
        App.injections.release<SignInQrCodeFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentSignInQrCodeBinding, viewModel: SignInQrCodeViewModel) {
        binding.viewModel = viewModel
    }
}