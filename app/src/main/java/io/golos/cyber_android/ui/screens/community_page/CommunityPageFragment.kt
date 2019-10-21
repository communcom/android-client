package io.golos.cyber_android.ui.screens.community_page

import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page.CommunityPageFragmentComponent
import io.golos.cyber_android.databinding.FragmentCommunityPageBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM

class CommunityPageFragment : FragmentBaseMVVM<FragmentCommunityPageBinding, CommunityPageModel, CommunityPageViewModel>() {

    override fun provideViewModelType(): Class<CommunityPageViewModel> = CommunityPageViewModel::class.java

    override fun provideLayout(): Int = R.layout.fragment_community_page

    override fun inject() = App.injections
        .get<CommunityPageFragmentComponent>()
        .inject(this)

    override fun linkViewModel(binding: FragmentCommunityPageBinding, viewModel: CommunityPageViewModel) {
        binding.viewModel = viewModel
    }

    companion object{

        fun newInstance(): CommunityPageFragment = CommunityPageFragment()
    }
}