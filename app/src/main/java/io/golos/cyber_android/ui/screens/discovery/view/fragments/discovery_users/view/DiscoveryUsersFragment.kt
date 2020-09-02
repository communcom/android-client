package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentDiscoveryUsersBinding
import io.golos.cyber_android.ui.dto.FollowersFilter
import io.golos.cyber_android.ui.screens.discovery.view.DiscoveryFragmentTab
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.di.DiscoveryUsersFragmentComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.view_model.DiscoveryUserViewModel
import io.golos.cyber_android.ui.screens.profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateToUserProfileCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.recycler_view.versioned.DynamicListWidget
import io.golos.domain.GlobalConstants
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.fragment_profile_followers.*

open class DiscoveryUsersFragment : FragmentBaseMVVM<FragmentDiscoveryUsersBinding, DiscoveryUserViewModel>() {

    companion object {
        fun newInstance() = DiscoveryUsersFragment().apply {

        }
    }

    override fun provideViewModelType(): Class<DiscoveryUserViewModel> = DiscoveryUserViewModel::class.java

    override fun layoutResId() = R.layout.fragment_discovery_users

    override fun linkViewModel(binding: FragmentDiscoveryUsersBinding, viewModel: DiscoveryUserViewModel) {
        binding.viewModel = viewModel
        val pFragment = parentFragment
        if(pFragment is DiscoveryFragmentTab){
            pFragment.getUsersLiveData().observe(viewLifecycleOwner, Observer {
                it?.let {
                    if(it.isEmpty()){
                        binding.emptyStub.visibility = View.VISIBLE
                        binding.emptyStub.setTitle(R.string.no_results)
                        binding.emptyStub.setExplanation(R.string.try_to_look_for_something_else)
                        binding.followingList.visibility = View.GONE
                    }else{
                        (followingList as DynamicListWidget).updateList(it)
                        binding.emptyStub.visibility = View.GONE
                        binding.followingList.visibility = View.VISIBLE
                    }
                }
            })
        }

    }

    override fun inject(key: String) =
        App.injections
            .get<DiscoveryUsersFragmentComponent>(
                key,
                GlobalConstants.PAGE_SIZE,
                false
            )
            .inject(this)

    override fun releaseInjection(key: String) = App.injections.release<DiscoveryUsersFragmentComponent>(key)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        followingList.setAdapterData(viewModel.pageSize, FollowersFilter.FOLLOWINGS, viewModel)
    }

    override fun processViewCommand(command: ViewCommand) {
        when(command) {
            is NavigateBackwardCommand -> requireActivity().onBackPressed()
            is NavigateToUserProfileCommand -> openUserProfile(command.userId)
        }
    }

    private fun openUserProfile(userId: UserIdDomain){
        getDashboardFragment(this)?.navigateToFragment(
            ProfileExternalUserFragment.newInstance(userId)
        )
    }
}