package io.golos.cyber_android.ui.screens.community_page_friends.view

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentCommunityPageFriendsBinding
import io.golos.cyber_android.ui.screens.community_page.dto.CommunityFriend
import io.golos.cyber_android.ui.screens.community_page_friends.di.CommunityPageFriendsComponent
import io.golos.cyber_android.ui.screens.community_page_friends.view_model.CommunityPageFriendsViewModel
import io.golos.cyber_android.ui.screens.profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToUserProfileCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.fragment_community_page_friends.*

open class CommunityPageFriendsFragment : FragmentBaseMVVM<FragmentCommunityPageFriendsBinding, CommunityPageFriendsViewModel>() {
    companion object {
        private const val FRIENDS = "FRIENDS"

        fun newInstance(friends: List<CommunityFriend>) =
            CommunityPageFriendsFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArray(FRIENDS, friends.toTypedArray())
                }
            }
    }

    override fun provideViewModelType(): Class<CommunityPageFriendsViewModel> = CommunityPageFriendsViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_community_page_friends

    override fun inject(key: String) = App.injections
        .get<CommunityPageFriendsComponent>(
            key,
            arguments!!.getParcelableArray(FRIENDS)!!.toList()
        )
        .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<CommunityPageFriendsComponent>(key)

    override fun linkViewModel(binding: FragmentCommunityPageFriendsBinding, viewModel: CommunityPageFriendsViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersList.setAdapterData(viewModel)
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateBackwardCommand -> requireActivity().onBackPressed()
            is NavigateToUserProfileCommand -> navigateToUserProfile(command.userId)
        }
    }

    private fun navigateToUserProfile(userId: UserIdDomain) {
        getDashboardFragment(this)?.showFragment(ProfileExternalUserFragment.newInstance(userId))
    }
}