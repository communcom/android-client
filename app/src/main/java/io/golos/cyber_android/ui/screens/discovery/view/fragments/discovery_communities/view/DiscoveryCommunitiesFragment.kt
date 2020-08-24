package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_communities.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentCommunitiesBinding
import io.golos.cyber_android.ui.screens.communities_list.view.CommunitiesListFragment
import io.golos.cyber_android.ui.screens.communities_list.view_model.CommunitiesListViewModel
import io.golos.cyber_android.ui.screens.discovery.view.DiscoveryFragmentTab
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_communities.di.DiscoveryCommunitiesFragmentComponent
import io.golos.domain.dto.UserIdDomain

open class DiscoveryCommunitiesFragment : CommunitiesListFragment() {
    companion object {

        private const val USER_ID = "USER_ID"

        fun newInstance(userId: UserIdDomain) = DiscoveryCommunitiesFragment().apply {
            arguments = Bundle().apply {
                putParcelable(USER_ID, userId)
            }
        }
    }

    override fun inject(key: String) {
        App.injections.get<DiscoveryCommunitiesFragmentComponent>(
            key,
            false,
            false,
            arguments!!.getParcelable(USER_ID),
            true
        ).inject(this)
    }


    override fun releaseInjection(key: String) {
        App.injections.release<DiscoveryCommunitiesFragmentComponent>(key)
    }

    override fun linkViewModel(binding: FragmentCommunitiesBinding, viewModel: CommunitiesListViewModel) {
        super.linkViewModel(binding, viewModel)
        val pFragment = parentFragment
        if(pFragment is DiscoveryFragmentTab){
            pFragment.getCommunitiesLiveData().observe(viewLifecycleOwner, Observer {
                if(it.isEmpty()){
                    binding.emptyStub.setTitle(R.string.no_results)
                    binding.emptyStub.setExplanation(R.string.try_to_look_for_something_else)
                    if(pFragment.isSearchStarted){
                        binding.mainList.visibility = View.GONE
                        binding.emptyStub.visibility = View.VISIBLE
                    } else {
                        binding.mainList.visibility = View.VISIBLE
                        binding.emptyStub.visibility = View.GONE
                    }
                }else{
                    updateList(it)
                    binding.emptyStub.visibility = View.GONE
                    binding.mainList.visibility = View.VISIBLE
                }
            })
        }
    }
}