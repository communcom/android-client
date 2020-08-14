package io.golos.cyber_android.ui.screens.discovery.view

import androidx.core.os.bundleOf
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentDiscoveryBinding
import io.golos.cyber_android.ui.screens.discovery.di.DiscoveryFragmentComponent
import io.golos.cyber_android.ui.screens.discovery.view.adapters.DiscoveryPagerAdapter
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.view.DiscoveryAllFragment
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_communities.view.DiscoveryCommunitiesFragment
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts.DiscoveryPostsFragment
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.view.DiscoveryUsersFragment
import io.golos.cyber_android.ui.screens.discovery.view_model.DiscoveryViewModel
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.utils.TabLayoutMediator
import io.golos.domain.dto.UserIdDomain

class DiscoveryFragmentTab : FragmentBaseMVVM<FragmentDiscoveryBinding, DiscoveryViewModel>() {
    private var userIdDomain: UserIdDomain? = null

    companion object {
        const val USER_ID = "USER_ID"
        fun newInstance(userIdDomain: UserIdDomain) = DiscoveryFragmentTab().apply {
            arguments = bundleOf(USER_ID to userIdDomain)
        }
    }

    override fun provideViewModelType(): Class<DiscoveryViewModel> = DiscoveryViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_discovery

    override fun linkViewModel(binding: FragmentDiscoveryBinding, viewModel: DiscoveryViewModel) {
        binding.viewModel = viewModel
        val items = createViewPagerItems()
        binding.discoveryPager.adapter = DiscoveryPagerAdapter(childFragmentManager, lifecycle, items)
        TabLayoutMediator(binding.discoveryTabs, binding.discoveryPager) { tab, position ->
            tab.text = items[position].title
            binding.discoveryPager.setCurrentItem(0, false)
        }.attach()
    }

    override fun inject(key: String) =
        App.injections.get<DiscoveryFragmentComponent>(key, arguments?.getParcelable(USER_ID)).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<DiscoveryFragmentComponent>(key)

    private fun createViewPagerItems(): ArrayList<DiscoveryPagerAdapter.FragmentPagerModel> {
        userIdDomain = arguments?.getParcelable(USER_ID)
        val list = ArrayList<DiscoveryPagerAdapter.FragmentPagerModel>()
        userIdDomain?.let {
            list.add(DiscoveryPagerAdapter.FragmentPagerModel(DiscoveryAllFragment.newInstance(it), getString(R.string.discovery_all)))
            list.add(DiscoveryPagerAdapter.FragmentPagerModel(DiscoveryCommunitiesFragment.newInstance(it), getString(R.string.communities)))
            list.add(DiscoveryPagerAdapter.FragmentPagerModel(DiscoveryUsersFragment(), getString(R.string.users)))
            list.add(DiscoveryPagerAdapter.FragmentPagerModel(DiscoveryPostsFragment(), getString(R.string.discovery_posts)))
        }
        return list
    }

}