package io.golos.cyber_android.ui.screens.ftue.view

import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentFtueBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.screens.ftue.di.FtueFragmentComponent
import io.golos.cyber_android.ui.screens.ftue.viewmodel.FtueViewModel

class FtueFragment : FragmentBaseMVVM<FragmentFtueBinding, FtueViewModel>() {

    override fun provideViewModelType(): Class<FtueViewModel> = FtueViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_ftue

    override fun inject() = App.injections.get<FtueFragmentComponent>()
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<FtueFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentFtueBinding, viewModel: FtueViewModel) {
        binding.viewModel = viewModel
    }
}