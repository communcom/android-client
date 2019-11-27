package io.golos.cyber_android.ui.screens.community_page.view

import android.annotation.SuppressLint
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
import io.golos.cyber_android.ui.common.extensions.loadCommunity
import io.golos.cyber_android.ui.common.formatters.counts.KiloCounterFormatter
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.mvvm.view_commands.BackCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.common.widgets.TabLineDrawable
import io.golos.cyber_android.ui.screens.community_page.child_pages.leads_list.view.LeadsListFragment
import io.golos.cyber_android.ui.screens.community_page.view_model.CommunityPageViewModel
import io.golos.cyber_android.ui.screens.community_page.dto.CommunityPage
import io.golos.cyber_android.ui.screens.community_page_about.CommunityPageAboutFragment
import io.golos.cyber_android.ui.screens.community_page_rules.CommunityPageRulesFragment
import io.golos.cyber_android.ui.screens.followers.FollowersFragment
import io.golos.cyber_android.ui.utils.*
import io.golos.cyber_android.ui.utils.toMMMM_DD_YYYY_Format
import io.golos.utils.toPluralInt
import kotlinx.android.synthetic.main.fragment_community_page.*
import kotlinx.android.synthetic.main.layout_community_header_members.*

class CommunityPageFragment : FragmentBaseMVVM<FragmentCommunityPageBinding, CommunityPageViewModel>() {

    override fun layoutResId(): Int = R.layout.fragment_community_page

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

    override fun inject() = App.injections
        .get<CommunityPageFragmentComponent>()
        .inject(this)

    override fun releaseInjection() {
        App.injections.release<CommunityPageFragmentComponent>()
    }


    override fun linkViewModel(binding: FragmentCommunityPageBinding, viewModel: CommunityPageViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTabLayout()
        observeViewModel()
        viewModel.start(getCommunityId())
        ivBack.setOnClickListener {
            viewModel.onBackPressed()
        }
    }

    override fun processViewCommand(command: ViewCommand) {
        super.processViewCommand(command)
        if (command is BackCommand) {
            requireFragmentManager().popBackStack()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        viewModel.communityPageLiveData.observe(this, Observer {
            tvCommunityName.text = it.name
            tvDescription.text = it.description
            setCommunityLogo(it.avatarUrl)
            setCommunityCover(it.coverUrl)
            setSubscriptionStatus(it.isSubscribed)
            appbar.visibility = View.VISIBLE
            vpContent.visibility = View.VISIBLE
            val leadsCount = it.leadsCount
            tvLeadsCount.text = KiloCounterFormatter.format(leadsCount)
            val membersCount = it.membersCount
            tvMemberCount.text = KiloCounterFormatter.format(membersCount)
            val friendsCount = it.friendsCount
            tvFriendsCountLabel.text = getString(R.string.friends_label, KiloCounterFormatter.format(friendsCount))
            val communityCurrency = it.communityCurrency
            tvCurrentCurrency.text = communityCurrency.currencyName
            tvCurrentCommunRate.text = communityCurrency.exchangeRate.toString()
            tvMembersLabel.text = resources.getQuantityString(R.plurals.plural_members, membersCount.toPluralInt())
            tvLeadsLabel.text = resources.getQuantityString(R.plurals.plural_leads, leadsCount.toPluralInt())
            tvFriendsLabel.text = resources.getQuantityText(R.plurals.plural_friends, friendsCount.toPluralInt())
            tvJoinTime.text = "${resources.getString(R.string.joined)} ${it.joinDate.toMMMM_DD_YYYY_Format()}"
            communityFollowersView.setFollowers(it.friends.take(FRIENDS_COUNT_MAX))
            ivNotificationCommunityControl.setOnClickListener {
                viewModel.onNotificationCommunityControlClicked()
            }
            ctvJoin.setOnClickListener {
                viewModel.changeJoinStatus()
            }
            btnRetry.setOnClickListener {
                viewModel.loadCommunityPage()
            }
            initViewPager(it)
        })

        viewModel.communityPageIsErrorLiveData.observe(this, Observer {
            if (it) {
                btnRetry.visibility = View.VISIBLE
            } else {
                btnRetry.visibility = View.INVISIBLE
            }
        })

        viewModel.communityPageIsLoadProgressLiveData.observe(this, Observer {
            if (it) {
                emptyPostProgressLoading.visibility = View.VISIBLE
            } else {
                emptyPostProgressLoading.visibility = View.INVISIBLE
            }
        })
    }

    private fun setCommunityCover(coverUrl: String?) {
        Glide.with(ivCommunityCover)
            .load(coverUrl)
            .apply(RequestOptions.centerCropTransform())
            .into(ivCommunityCover)
    }

    private fun setCommunityLogo(communityLogo: String?) {
        ivCommunityLogo.loadCommunity(communityLogo)
    }

    private fun setSubscriptionStatus(isSubscribed: Boolean) {
        ctvJoin.isChecked = isSubscribed
        if (isSubscribed) {
            ctvJoin.text = getString(R.string.joined_to_community)
            ivNotificationCommunityControl.visibility = View.VISIBLE
        } else {
            ctvJoin.text = getString(R.string.join_to_community)
            ivNotificationCommunityControl.visibility = View.INVISIBLE
        }
    }

    private fun initTabLayout() {
        tabLayout.apply {
            setupWithViewPager(vpContent)
            setSelectedTabIndicator(TabLineDrawable(requireContext()))
            setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), R.color.blue))
        }
    }

    private fun initViewPager(communityPage: CommunityPage) {
        fragmentPagesList = createPageFragmentsList(communityPage)
        vpContent.adapter = communityPagerTabAdapter
        vpContent.offscreenPageLimit = 4
    }

    private fun createPageFragmentsList(communityPage: CommunityPage): MutableList<Fragment> {
        val fragmentPagesList = ArrayList<Fragment>()
        fragmentPagesList.add(FollowersFragment.newInstance())
        fragmentPagesList.add(LeadsListFragment.newInstance(arguments!!.getString(ARG_COMMUNITY_ID, io.golos.utils.EMPTY)))
        fragmentPagesList.add(CommunityPageAboutFragment.newInstance(communityPage.description))
        fragmentPagesList.add(CommunityPageRulesFragment.newInstance(communityPage.rules))
        return fragmentPagesList
    }

    private fun getCommunityId() = arguments!!.getString(ARG_COMMUNITY_ID, io.golos.utils.EMPTY)

    companion object {

        private const val ARG_COMMUNITY_ID = "ARG_COMMUNITY_ID"

        fun newInstance(communityId: String): CommunityPageFragment {

            val communityPageFragment = CommunityPageFragment()
            val bundle = Bundle()
            bundle.putString(ARG_COMMUNITY_ID, communityId)
            communityPageFragment.arguments = bundle
            return communityPageFragment
        }

        private const val FRIENDS_COUNT_MAX = 3
    }
}