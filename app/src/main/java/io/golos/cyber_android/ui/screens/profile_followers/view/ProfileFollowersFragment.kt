package io.golos.cyber_android.ui.screens.profile_followers.view

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.profile_followers.di.ProfileFollowersFragmentComponent
import io.golos.cyber_android.databinding.FragmentProfileFollowersBinding
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.profile_followers.view_model.ProfileFollowersViewModel
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.UserDomain
import kotlinx.android.synthetic.main.fragment_profile_followers.*

open class ProfileFollowersFragment : FragmentBaseMVVM<FragmentProfileFollowersBinding, ProfileFollowersViewModel>() {
    companion object {
        private const val FILTER = "FILTER"
        private const val MUTUAL_USERS = "MUTUAL_USERS"

        fun newInstance(filter: FollowersFilter, mutualUsers: List<UserDomain>) = ProfileFollowersFragment().apply {
            arguments = Bundle().apply {
                putInt(FILTER, filter.value)
                putParcelableArray(MUTUAL_USERS, mutualUsers.toTypedArray())
            }
        }
    }

    override fun provideViewModelType(): Class<ProfileFollowersViewModel> = ProfileFollowersViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_profile_followers

    override fun inject(key: String) =
        App.injections
            .get<ProfileFollowersFragmentComponent>(
                key,
                FollowersFilter.create(arguments!!.getInt(FILTER)),
                GlobalConstants.PAGE_SIZE,
                arguments!!.getParcelableArray(MUTUAL_USERS)!!.toList())
            .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<ProfileFollowersFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentProfileFollowersBinding, viewModel: ProfileFollowersViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mutualList.setAdapterData(viewModel)
        followersList.setAdapterData(viewModel.pageSize, FollowersFilter.FOLLOWERS, viewModel)
        followingList.setAdapterData(viewModel.pageSize, FollowersFilter.FOLLOWINGS, viewModel)
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateBackwardCommand -> requireActivity().onBackPressed()
        }
    }
}