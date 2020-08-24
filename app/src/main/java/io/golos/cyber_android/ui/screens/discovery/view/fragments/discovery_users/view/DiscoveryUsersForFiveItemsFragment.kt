package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.view

import android.os.Bundle
import android.view.View
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentDiscoveryUsersBinding
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.di.DiscoveryUsersForFiveItemsComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.view_model.DiscoveryUserViewModel
import io.golos.cyber_android.ui.screens.profile_followers.dto.FollowersListItem
import io.golos.cyber_android.ui.shared.recycler_view.versioned.DynamicListWidget
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.fragment_profile_followers.*

class DiscoveryUsersForFiveItemsFragment : DiscoveryUsersFragment() {
    companion object {

        private const val USER_ID = "USER_ID"

        fun newInstance(userId: UserIdDomain) = DiscoveryUsersForFiveItemsFragment().apply {
            arguments = Bundle().apply {
                putParcelable(USER_ID, userId)
            }
        }
    }

    private lateinit var mBinding: FragmentDiscoveryUsersBinding

    override fun inject(key: String) = App.injections.get<DiscoveryUsersForFiveItemsComponent>(key, 5, true).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<DiscoveryUsersForFiveItemsComponent>(key)

    override fun linkViewModel(binding: FragmentDiscoveryUsersBinding, viewModel: DiscoveryUserViewModel) {
        super.linkViewModel(binding, viewModel)
        mBinding = binding
    }

    fun updateList(it: List<FollowersListItem>) {
        if (it.isEmpty()) {
            mBinding.emptyStub.visibility = View.VISIBLE
            mBinding.emptyStub.setTitle(R.string.no_results)
            mBinding.emptyStub.setExplanation(R.string.try_to_look_for_something_else)
            mBinding.followingList.visibility = View.GONE
        } else {
            (followingList as DynamicListWidget).updateList(it)
            mBinding.emptyStub.visibility = View.GONE
            mBinding.followingList.visibility = View.VISIBLE
        }
    }
}