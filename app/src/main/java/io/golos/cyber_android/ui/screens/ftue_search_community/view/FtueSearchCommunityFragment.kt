package io.golos.cyber_android.ui.screens.ftue_search_community.view

import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentFtueSearchCommunityBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.screens.ftue_search_community.di.FtueSearchCommunityFragmentComponent
import io.golos.cyber_android.ui.screens.ftue_search_community.viewmodel.FtueSearchCommunityViewModel

class FtueSearchCommunityFragment: FragmentBaseMVVM<FragmentFtueSearchCommunityBinding, FtueSearchCommunityViewModel>(){

    override fun provideViewModelType(): Class<FtueSearchCommunityViewModel> = FtueSearchCommunityViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_ftue_search_community

    override fun inject()  = App.injections.get<FtueSearchCommunityFragmentComponent>()
        .inject(this)


    override fun releaseInjection() {
        App.injections.release<FtueSearchCommunityFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentFtueSearchCommunityBinding, viewModel: FtueSearchCommunityViewModel) {
        binding.viewModel = viewModel
    }

    companion object {

        fun newInstance(): Fragment = FtueSearchCommunityFragment()
    }
}