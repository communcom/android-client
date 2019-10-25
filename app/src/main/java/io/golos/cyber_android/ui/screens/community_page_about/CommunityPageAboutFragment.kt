package io.golos.cyber_android.ui.screens.community_page_about

import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page_about.CommunityPageAboutFragmentComponent
import io.golos.cyber_android.databinding.FragmentCommunityPageAboutBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM

class CommunityPageAboutFragment : FragmentBaseMVVM<FragmentCommunityPageAboutBinding, CommunityPageAboutModel, CommunityPageAboutViewModel>() {

    override fun provideViewModelType(): Class<CommunityPageAboutViewModel> = CommunityPageAboutViewModel::class.java

    override fun provideLayout(): Int = R.layout.fragment_community_page_about

    override fun inject() = App.injections
    .get<CommunityPageAboutFragmentComponent>()
    .inject(this)

    override fun linkViewModel(binding: FragmentCommunityPageAboutBinding, viewModel: CommunityPageAboutViewModel) {
        binding.viewModel = viewModel
    }

    companion object{

        fun newInstance(): Fragment{
            return CommunityPageAboutFragment()
        }
    }
}