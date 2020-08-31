package io.golos.cyber_android.ui.screens.discovery.view

import android.os.Handler
import android.text.Editable
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentDiscoveryBinding
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.cyber_android.ui.screens.discovery.di.DiscoveryFragmentComponent
import io.golos.cyber_android.ui.screens.discovery.view.adapters.DiscoveryPagerAdapter
import io.golos.cyber_android.ui.screens.discovery.view.adapters.DiscoveryRecyclerAdapter
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.view.DiscoveryAllFragment
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_communities.view.DiscoveryCommunitiesFragment
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts.DiscoveryPostsFragment
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.view.DiscoveryUsersFragment
import io.golos.cyber_android.ui.screens.discovery.view_model.DiscoveryViewModel
import io.golos.cyber_android.ui.screens.profile.view.ProfileExternalUserFragment
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.utils.TabLayoutMediator
import io.golos.cyber_android.ui.shared.utils.TextWatcherBase
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.UserIdDomain

class DiscoveryFragmentTab : FragmentBaseMVVM<FragmentDiscoveryBinding, DiscoveryViewModel>() {
    private var userIdDomain: UserIdDomain? = null
    private lateinit var mBinding: FragmentDiscoveryBinding
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var mAdapter:DiscoveryRecyclerAdapter
    var isSearchStarted = false
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
        mBinding = binding
        val items = createViewPagerItems()
        binding.apply {
            showAllResult.setOnClickListener { binding.isSearchStarted = false }
            discoveryPager.adapter = DiscoveryPagerAdapter(childFragmentManager, lifecycle, items)
            discoveryPager.offscreenPageLimit = items.size
            cancelText.setOnClickListener { binding.searchField.setText("") }
        }
        observeToLiveDatas()
        TabLayoutMediator(binding.discoveryTabs, binding.discoveryPager) { tab, position ->
            tab.text = items[position].title
            binding.discoveryPager.setCurrentItem(0, false)
        }.attach()
        initSearchEngine(binding.searchField)
    }

    private fun observeToLiveDatas() {
        viewModel.searchPreview.observe(viewLifecycleOwner, Observer {
            if(!::mAdapter.isInitialized){
                mAdapter = DiscoveryRecyclerAdapter(::openUserProfile,::openCommunityPage)
            }
            mBinding.combinedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            mBinding.combinedRecyclerView.adapter = mAdapter
            mAdapter.submitList(it)
        })
    }

    private fun openCommunityPage(communityIdDomain: CommunityIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(
            CommunityPageFragment.newInstance(communityIdDomain)
        )
    }

    private fun openUserProfile(userIdDomain: UserIdDomain) {
        getDashboardFragment(this)?.navigateToFragment(
            ProfileExternalUserFragment.newInstance(userIdDomain)
        )
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

    private fun initSearchEngine(searchField: AppCompatEditText) {
        searchField.addTextChangedListener(object : TextWatcherBase() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                isSearchStarted = s.toString().isNotEmpty()
                mBinding.isSearchStarted = isSearchStarted
                if(!isSearchStarted){
                    mBinding.discoveryPager.adapter = DiscoveryPagerAdapter(childFragmentManager, lifecycle, createViewPagerItems())
                }
                if (!::handler.isInitialized) {
                    handler = Handler()
                }
                if (!::runnable.isInitialized) {
                    runnable = Runnable {
                        viewModel.search(s.toString())
                    }
                }
                if (s.toString().isNotEmpty()) {
                    handler.removeCallbacks(runnable)
                    handler.postDelayed(runnable, 500)
                }
            }
        })
    }

    fun getCommunitiesLiveData() = viewModel.communities

    fun getPostsLiveData() = viewModel.posts

    fun getUsersLiveData() = viewModel.users

    fun switchTab(position: Int) {
        mBinding.discoveryPager.setCurrentItem(position, true)
    }
}