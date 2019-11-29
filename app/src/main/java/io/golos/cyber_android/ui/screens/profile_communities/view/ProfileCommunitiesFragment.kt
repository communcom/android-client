package io.golos.cyber_android.ui.screens.profile_communities.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_communities.ProfileCommunitiesFragmentComponent
import io.golos.cyber_android.databinding.FragmentProfileCommunitiesBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.dto.ProfileCommunities
import io.golos.cyber_android.ui.screens.profile_communities.view_model.ProfileCommunitiesViewModel

class ProfileCommunitiesFragment : FragmentBaseMVVM<FragmentProfileCommunitiesBinding, ProfileCommunitiesViewModel>() {
    companion object {
        private const val SOURCE_DATA = "SOURCE_DATA"

        fun newInstance(sourceData: ProfileCommunities) =
            ProfileCommunitiesFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SOURCE_DATA, sourceData)
                }
            }
    }

    override fun provideViewModelType(): Class<ProfileCommunitiesViewModel> = ProfileCommunitiesViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_communities

    override fun inject() =
        App.injections
        .get<ProfileCommunitiesFragmentComponent>(arguments!!.getParcelable<ProfileCommunities>(SOURCE_DATA))
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<ProfileCommunitiesFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentProfileCommunitiesBinding, viewModel: ProfileCommunitiesViewModel) {
        binding.viewModel = viewModel
    }

//    override fun processViewCommand(command: ViewCommand) {
//        when(command) {
//            is BackCommand -> requireActivity().onBackPressed()
//            is PassResultCommand -> passResult(command.text)
//            is PrepareToCloseCommand -> prepareToClose()
//        }
//    }
}