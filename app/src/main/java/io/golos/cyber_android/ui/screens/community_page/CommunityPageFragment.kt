package io.golos.cyber_android.ui.screens.community_page

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page.CommunityPageFragmentComponent
import io.golos.cyber_android.databinding.FragmentCommunityPageBinding
import io.golos.cyber_android.ui.common.formatters.counts.KiloCounterFormatter
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.widgets.TabLineDrawable
import io.golos.cyber_android.ui.screens.followers.FollowersFragment
import io.golos.cyber_android.utils.EMPTY
import io.golos.cyber_android.utils.toMM_DD_YYYY_Format
import kotlinx.android.synthetic.main.fragment_community_page.*
import kotlinx.android.synthetic.main.fragment_community_page.tabLayout
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_community_header_members.*

class CommunityPageFragment : FragmentBaseMVVM<FragmentCommunityPageBinding, CommunityPageModel, CommunityPageViewModel>() {

    private var fragmentPagesList: List<Fragment> = ArrayList()

    private val tabTitles by lazy {
        requireContext().resources.getStringArray(R.array.community_page_tab_titles)
    }

    private val communityPagerTabAdapter by lazy {

        object : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

            override fun getPageTitle(position: Int): CharSequence? {
                return tabTitles.getOrNull(position)
            }

            override fun getItem(position: Int): Fragment = fragmentPagesList[position]

            override fun getCount(): Int = fragmentPagesList.size

        }
    }

    override fun provideViewModelType(): Class<CommunityPageViewModel> = CommunityPageViewModel::class.java

    override fun provideLayout(): Int = R.layout.fragment_community_page

    override fun inject() = App.injections
        .get<CommunityPageFragmentComponent>()
        .inject(this)

    override fun linkViewModel(binding: FragmentCommunityPageBinding, viewModel: CommunityPageViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTablayout()
        initViewPager()
        observeViewModel()
        viewModel.start(arguments!!.getString(ARG_COMMUNITY_ID, EMPTY))
    }

    private fun observeViewModel() {
        viewModel.communityPageLiveData.observe(this, Observer {
            tvCommunityName.text = it.name
            tvDescription.text = it.description
            Glide.with(ivCommunityLogo)
                .load(it.avatarUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(ivCommunityLogo)
            Glide.with(ivCommunityCover)
                .load(it.coverUrl)
                .apply(RequestOptions.centerInsideTransform())
                .into(ivCommunityCover)
            ctvJoin.isChecked = it.isSubscribed
            if(it.isSubscribed){
                ctvJoin.text = getString(R.string.joined_to_community)
            } else{
                ctvJoin.text = getString(R.string.join_to_community)
            }
            appbar.visibility = View.VISIBLE
            vpContent.visibility = View.VISIBLE
            val leadsCount = it.leadsCount
            tvLeadsCount.text = KiloCounterFormatter().format(leadsCount)
            val membersCount = it.membersCount
            tvMemberCount.text = KiloCounterFormatter().format(membersCount)
            val friendsCount = it.friendsCount
            tvFriendsCountLabel.text = getString(R.string.friends_label, KiloCounterFormatter().format(friendsCount))
            val communityCurrency = it.communityCurrency
            tvCurrentCurrency.text = communityCurrency.currencyName
            tvCurrentCommunRate.text = communityCurrency.exchangeRate.toString()
            val pluralMembersCount: Int = if (membersCount > 10) 10 else membersCount.toInt()
            val pluralLeadsCount: Int = if (leadsCount > 10) 10 else leadsCount.toInt()
            val pluralFriendsCount: Int = if (friendsCount > 10) 10 else friendsCount.toInt()
            tvMembersLabel.text = resources.getQuantityString(R.plurals.plural_members, pluralMembersCount)
            tvLeadsLabel.text = resources.getQuantityString(R.plurals.plural_leads, pluralLeadsCount)
            tvFriendsLabel.text = resources.getQuantityText(R.plurals.plural_friends, pluralFriendsCount)
            tvJoinTime.text = it.joinDate.toMM_DD_YYYY_Format()
            communityFollowersView.setFollowers(it.friends.take(3))
        })

        viewModel.communityPageIsErrorLiveData.observe(this, Observer {
            if(it){
                btnRetry.visibility = View.VISIBLE
            } else{
                btnRetry.visibility = View.INVISIBLE
            }
        })

        viewModel.communityPageIsLoadProgressLiveData.observe(this, Observer {
            if(it){
                generalProgressLoading.visibility = View.VISIBLE
            } else{
                generalProgressLoading.visibility = View.INVISIBLE
            }
        })
    }

    private fun initTablayout() {
        tabLayout.apply {
            setupWithViewPager(vpContent)
            setSelectedTabIndicator(TabLineDrawable(requireContext()))
            setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), R.color.blue))
        }
    }

    private fun initViewPager() {
        fragmentPagesList = createPageFragmentsList()
        vpContent.adapter = communityPagerTabAdapter
    }

    private fun createPageFragmentsList(): MutableList<Fragment> {
        val fragmentPagesList = ArrayList<Fragment>()
        fragmentPagesList.add(FollowersFragment.newInstance())
        fragmentPagesList.add(FollowersFragment.newInstance())
        fragmentPagesList.add(FollowersFragment.newInstance())
        fragmentPagesList.add(FollowersFragment.newInstance())
        return fragmentPagesList
    }

    companion object {

        private const val ARG_COMMUNITY_ID = "ARG_COMMUNITY_ID"

        fun newInstance(communityId: String): CommunityPageFragment {

            val communityPageFragment = CommunityPageFragment()
            val bundle = Bundle()
            bundle.putString(ARG_COMMUNITY_ID, communityId)
            communityPageFragment.arguments = bundle
            return communityPageFragment
        }
    }
}