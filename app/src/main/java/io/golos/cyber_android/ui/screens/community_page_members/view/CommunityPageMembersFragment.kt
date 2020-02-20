package io.golos.cyber_android.ui.screens.community_page_members.view

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentCommunityPageMembersBinding
import io.golos.cyber_android.ui.screens.community_page_members.di.CommunityPageMembersComponent
import io.golos.cyber_android.ui.screens.community_page_members.view_model.CommunityPageMembersViewModel
import io.golos.cyber_android.ui.screens.profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToUserProfileCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.fragment_community_page_members.*

open class CommunityPageMembersFragment : FragmentBaseMVVM<FragmentCommunityPageMembersBinding, CommunityPageMembersViewModel>() {
    companion object {
        private const val COMMUNITY_ID = "COMMUNITY_ID"

        fun newInstance(communityId: String) =
            CommunityPageMembersFragment().apply {
                arguments = Bundle().apply {
                    putString(COMMUNITY_ID, communityId)
                }
            }
    }

    override fun provideViewModelType(): Class<CommunityPageMembersViewModel> = CommunityPageMembersViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_community_page_members

    override fun inject(key: String) = App.injections
        .get<CommunityPageMembersComponent>(
            key,
            arguments!!.getString(COMMUNITY_ID)!!,
            GlobalConstants.PAGE_SIZE
        )
        .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<CommunityPageMembersComponent>(key)

    override fun linkViewModel(binding: FragmentCommunityPageMembersBinding, viewModel: CommunityPageMembersViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersList.setAdapterData(viewModel.pageSize, viewModel)
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateBackwardCommand -> requireActivity().onBackPressed()
            is NavigateToUserProfileCommand -> navigateToUserProfile(command.userId)
        }
    }

    private fun navigateToUserProfile(userId: UserIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(ProfileExternalUserFragment.newInstance(userId))
    }
}