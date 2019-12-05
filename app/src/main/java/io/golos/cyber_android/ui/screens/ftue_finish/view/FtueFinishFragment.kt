package io.golos.cyber_android.ui.screens.ftue_finish.view

import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentFtueFinishBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.screens.ftue_finish.di.FtueFinishFragmentComponent
import io.golos.cyber_android.ui.screens.ftue_finish.view_model.FtueFinishViewModel

class FtueFinishFragment : FragmentBaseMVVM<FragmentFtueFinishBinding, FtueFinishViewModel>(){

    override fun provideViewModelType(): Class<FtueFinishViewModel> = FtueFinishViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_ftue_finish

    override fun inject() = App.injections.get<FtueFinishFragmentComponent>()
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<FtueFinishFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentFtueFinishBinding, viewModel: FtueFinishViewModel) {
        binding.viewModel = viewModel
    }

    companion object {

        fun newInstance(): Fragment = FtueFinishFragment()
    }
}