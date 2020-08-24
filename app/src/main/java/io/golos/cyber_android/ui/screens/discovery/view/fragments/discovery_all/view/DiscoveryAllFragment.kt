package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentDiscoveryAllBinding
import io.golos.cyber_android.ui.screens.discovery.view.DiscoveryFragmentTab
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discover_five_communities.view.DiscoveryFiveCommunitiesFragment
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.di.DiscoveryAllFragmentComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.view_model.DiscoveryAllViewModel
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.view.DiscoveryUsersForFiveItemsFragment
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.domain.dto.UserIdDomain

class DiscoveryAllFragment : FragmentBaseMVVM<FragmentDiscoveryAllBinding, DiscoveryAllViewModel>() {

    companion object {

        private const val USER_ID = "USER_ID"

        fun newInstance(userId: UserIdDomain) = DiscoveryAllFragment().apply {
            arguments = Bundle().apply {
                putParcelable(USER_ID, userId)
            }
        }
    }

    private lateinit var discoveryUsersForFiveItemsFragment: DiscoveryUsersForFiveItemsFragment
    private lateinit var discoverCommunitiesFiveItemsFragment: DiscoveryFiveCommunitiesFragment

    override fun provideViewModelType(): Class<DiscoveryAllViewModel> = DiscoveryAllViewModel::class.java

    override fun layoutResId() = R.layout.fragment_discovery_all

    override fun linkViewModel(binding: FragmentDiscoveryAllBinding, viewModel: DiscoveryAllViewModel) {
        val fragmentTag = "DISCOVERY_USERS"
        val fragmentFiveCommunitiesTag = "DISCOVERY_FIVE_COMMUNITIES"

        val transaction = childFragmentManager.beginTransaction()

        childFragmentManager.findFragmentByTag(fragmentTag)?.let { transaction.remove(it) }
        childFragmentManager.findFragmentByTag(fragmentFiveCommunitiesTag)?.let { transaction.remove(it) }

        val pFragment = parentFragment
        if (pFragment is DiscoveryFragmentTab) {
            binding.vAllCommunities.setOnClickListener { pFragment.switchTab(1) }
            binding.vAllUsers.setOnClickListener { pFragment.switchTab(3) }
            pFragment.getUsersLiveData().observe(viewLifecycleOwner, Observer {
                /*discoveryUsersForFiveItemsFragment.updateList(it)*/
            })
            pFragment.getCommunitiesLiveData().observe(viewLifecycleOwner, Observer {
                /*if(it.isNotEmpty()) {
                    binding.communitiesLabel.visibility = View.VISIBLE
                    binding.vAllCommunities.visibility = View.VISIBLE
                    discoverCommunitiesFiveItemsFragment.updateList(it)
                }else {
                    binding.communitiesLabel.visibility = View.GONE
                    binding.vAllCommunities.visibility = View.GONE
                }*/
            })
        }

        transaction.add(R.id.usersList, DiscoveryUsersForFiveItemsFragment().also {
            discoveryUsersForFiveItemsFragment = it
        }, fragmentTag)
        transaction.add(R.id.communitiesList, DiscoveryFiveCommunitiesFragment().also {
            discoverCommunitiesFiveItemsFragment = it
        }, fragmentFiveCommunitiesTag)
        transaction.commit()
    }

    override fun inject(key: String) {
        App.injections.get<DiscoveryAllFragmentComponent>(key, null).inject(this)
    }

    override fun releaseInjection(key: String) {
        App.injections.release<DiscoveryAllFragmentComponent>(key)
    }

}