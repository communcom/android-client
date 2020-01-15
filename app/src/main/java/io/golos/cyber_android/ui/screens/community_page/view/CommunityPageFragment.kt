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
import io.golos.cyber_android.databinding.FragmentCommunityPageBinding
import io.golos.cyber_android.ui.screens.community_page.di.CommunityPageFragmentComponent
import io.golos.cyber_android.ui.screens.community_page.dto.CommunityPage
import io.golos.cyber_android.ui.screens.community_page.dto.SwitchToLeadsTabCommand
import io.golos.cyber_android.ui.screens.community_page.view_model.CommunityPageViewModel
import io.golos.cyber_android.ui.screens.community_page_about.CommunityPageAboutFragment
import io.golos.cyber_android.ui.screens.community_page_leaders_list.view.LeadsListFragment
import io.golos.cyber_android.ui.screens.community_page_post.view.CommunityPostFragment
import io.golos.cyber_android.ui.screens.community_page_rules.CommunityPageRulesFragment
import io.golos.cyber_android.ui.shared.formatters.counts.KiloCounterFormatter
import io.golos.cyber_android.ui.shared.glide.loadCommunity
import io.golos.cyber_android.ui.shared.glide.loadCover
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.utils.toMMMM_DD_YYYY_Format
import io.golos.cyber_android.ui.shared.widgets.TabLineDrawable
import io.golos.utils.toPluralInt
import kotlinx.android.synthetic.main.fragment_community_page.*
import kotlinx.android.synthetic.main.layout_community_header_members.*

class CommunityPageFragment : FragmentBaseMVVM<FragmentCommunityPageBinding, CommunityPageViewModel>() {
    companion object {
        private const val ARG_COMMUNITY_ID = "ARG_COMMUNITY_ID"

        fun getBundle(communityId: String): Bundle{
            val bundle = Bundle()
            bundle.putString(ARG_COMMUNITY_ID, communityId)
            return bundle
        }

        fun newInstance(communityId: String): CommunityPageFragment {
            val communityPageFragment = CommunityPageFragment()
            val bundle = Bundle()
            bundle.putString(ARG_COMMUNITY_ID, communityId)
            communityPageFragment.arguments = bundle
            return communityPageFragment
        }

        private const val FRIENDS_COUNT_MAX = 3
    }

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

        ivBack.setOnClickListener {
            viewModel.onBackPressed()
        }
        noConnection.setOnReconnectClickListener {
            viewModel.loadCommunityPage()
        }

        viewModel.start(getCommunityId())
    }

    override fun processViewCommand(command: ViewCommand) {
        super.processViewCommand(command)

        when(command) {
            is NavigateBackwardCommand -> requireFragmentManager().popBackStack()
            is SwitchToLeadsTabCommand -> switchToTab(1)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        viewModel.communityPageLiveData.observe(this, Observer {
            tvCommunityName.text = it.name
            tvDescription.text = it.description

            tvDescription.visibility = if(it.description.isNullOrBlank()) View.GONE else View.VISIBLE

            setCommunityLogo(it.avatarUrl)
            setCommunityCover(it.coverUrl)
            setSubscriptionStatus(it.isSubscribed)
            appbar.visibility = View.VISIBLE
            vpContent.visibility = View.VISIBLE

            tvLeadsCount.text = KiloCounterFormatter.format(it.leadsCount)
            tvLeadsLabel.text = resources.getQuantityString(R.plurals.plural_leads, it.leadsCount.toPluralInt())

            tvLeadsLabel.setOnClickListener { viewModel.onLeadsLabelClick() }
            tvLeadsCount.setOnClickListener { viewModel.onLeadsLabelClick() }

            val membersCount = it.membersCount
            tvMemberCount.text = KiloCounterFormatter.format(membersCount)

            if(it.friendsCount > 0) {
                tvFriendsCountLabel.visibility = View.VISIBLE
                tvFriendsLabel.visibility = View.VISIBLE

                tvFriendsCountLabel.text = getString(R.string.friends_label, KiloCounterFormatter.format(it.friendsCount))
                tvFriendsLabel.text = resources.getQuantityText(R.plurals.plural_friends, it.friendsCount.toPluralInt())
            } else {
                tvFriendsCountLabel.visibility = View.GONE
                tvFriendsLabel.visibility = View.GONE
            }

            val communityCurrency = it.communityCurrency
            tvCurrentCurrency.text = communityCurrency.currencyName
            tvCurrentCommunRate.text = communityCurrency.exchangeRate.toString()
            tvMembersLabel.text = resources.getQuantityString(R.plurals.plural_members, membersCount.toPluralInt())

            tvJoinTime.text = "${resources.getString(R.string.joined)} ${it.joinDate.toMMMM_DD_YYYY_Format()}"
            communityFollowersView.setFollowers(it.friends.take(FRIENDS_COUNT_MAX))
            ctvJoin.setOnClickListener { viewModel.changeJoinStatus() }
            initViewPager(it)
        })

        viewModel.communityPageIsErrorLiveData.observe(this, Observer {
            if (it) {
                noConnection.visibility = View.VISIBLE
            } else {
                noConnection.visibility = View.INVISIBLE
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
        ivCommunityCover.loadCover(coverUrl)
    }

    private fun setCommunityLogo(communityLogo: String?) {
        ivCommunityLogo.loadCommunity(communityLogo)
    }

    private fun setSubscriptionStatus(isSubscribed: Boolean) {
        ctvJoin.isChecked = isSubscribed
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
        fragmentPagesList.add(
            CommunityPostFragment.newInstance(communityPage.communityId, communityPage.alias)
        )
        fragmentPagesList.add(
            LeadsListFragment.newInstance(arguments!!.getString(ARG_COMMUNITY_ID, io.golos.utils.EMPTY))
        )
        fragmentPagesList.add(
            CommunityPageAboutFragment.newInstance(communityPage.description)
        )
        fragmentPagesList.add(
            CommunityPageRulesFragment.newInstance(communityPage.rules)
        )
        return fragmentPagesList
    }

    private fun getCommunityId() = arguments!!.getString(ARG_COMMUNITY_ID, io.golos.utils.EMPTY)

    private fun switchToTab(tabIndex: Int) = tabLayout.getTabAt(tabIndex)!!.select()
}