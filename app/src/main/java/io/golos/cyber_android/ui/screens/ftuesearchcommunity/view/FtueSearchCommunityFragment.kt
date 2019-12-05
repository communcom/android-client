package io.golos.cyber_android.ui.screens.ftuesearchcommunity.view

import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentFtueSearchCommunityBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.screens.ftue.di.FtueFragmentComponent
import io.golos.cyber_android.ui.screens.ftuesearchcommunity.di.FtueSearchCommunityComponent
import io.golos.cyber_android.ui.screens.ftuesearchcommunity.viewmodel.FtueSearchCommunityViewModel

class FtueSearchCommunityFragment: FragmentBaseMVVM<FragmentFtueSearchCommunityBinding, FtueSearchCommunityViewModel>(){

    override fun provideViewModelType(): Class<FtueSearchCommunityViewModel> = FtueSearchCommunityViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_ftue_search_community

    override fun inject()  = App.injections.get<FtueSearchCommunityComponent>()
        .inject(this)


    override fun releaseInjection() {
        App.injections.release<FtueSearchCommunityComponent>()
    }

    override fun linkViewModel(binding: FragmentFtueSearchCommunityBinding, viewModel: FtueSearchCommunityViewModel) {
        binding.viewModel = viewModel
    }

    companion object {

        fun newInstance(): Fragment = FtueSearchCommunityFragment()
    }
}