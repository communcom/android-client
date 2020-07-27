package io.golos.cyber_android.ui.screens.community_page.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import com.facebook.appevents.codeless.internal.ViewHierarchy.setOnClickListener
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentCommunityPageBinding
import io.golos.cyber_android.ui.dialogs.CommunitySettingsDialog
import io.golos.cyber_android.ui.screens.community_get_points.GetCommunityPointsFragment
import io.golos.cyber_android.ui.screens.community_page.di.CommunityPageFragmentComponent
import io.golos.cyber_android.ui.screens.community_page.dto.*
import io.golos.cyber_android.ui.screens.community_page.view_model.CommunityPageViewModel
import io.golos.cyber_android.ui.screens.community_page_about.CommunityPageAboutFragment
import io.golos.cyber_android.ui.screens.community_page_friends.view.CommunityPageFriendsFragment
import io.golos.cyber_android.ui.screens.community_page_leaders_list.view.LeadsListFragment
import io.golos.cyber_android.ui.screens.community_page_members.view.CommunityPageMembersFragment
import io.golos.cyber_android.ui.screens.community_page_post.view.CommunityPostFragment
import io.golos.cyber_android.ui.screens.community_page_rules.CommunityPageRulesFragment
import io.golos.cyber_android.ui.shared.glide.loadCommunity
import io.golos.cyber_android.ui.shared.glide.loadCover
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.mvvm.view_commands.NavigateBackwardCommand
import io.golos.cyber_android.ui.shared.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.shared.popups.no_connection.NoConnectionPopup
import io.golos.cyber_android.ui.shared.utils.shareMessage
import io.golos.cyber_android.ui.shared.utils.toMMMM_DD_YYYY_Format
import io.golos.cyber_android.ui.shared.widgets.TabLineDrawable
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.WalletCommunityBalanceRecordDomain
import io.golos.utils.format.KiloCounterFormatter
import io.golos.utils.helpers.toPluralInt
import kotlinx.android.synthetic.main.fragment_community_page.*
import kotlinx.android.synthetic.main.layout_community_header_members.*

class CommunityPageFragment : FragmentBaseMVVM<FragmentCommunityPageBinding, CommunityPageViewModel>() {
    companion object {
        private const val ARG_COMMUNITY_ID = "ARG_COMMUNITY_ID"

        fun newInstance(communityId: CommunityIdDomain): CommunityPageFragment {
            val communityPageFragment = CommunityPageFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_COMMUNITY_ID, communityId)
            communityPageFragment.arguments = bundle
            return communityPageFragment
        }

        private const val FRIENDS_COUNT_MAX = 3
    }

    override fun layoutResId(): Int = R.layout.fragment_community_page

    private var fragmentPagesList: MutableList<Fragment> = ArrayList()

    private val tabTitles by lazy {
        requireContext().resources.getStringArray(R.array.community_page_tab_titles)
    }

    override fun provideViewModelType(): Class<CommunityPageViewModel> = CommunityPageViewModel::class.java

    override fun inject(key: String) = App.injections.get<CommunityPageFragmentComponent>(
        key, arguments!!.getParcelable<CommunityIdDomain>(ARG_COMMUNITY_ID)).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<CommunityPageFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentCommunityPageBinding, viewModel: CommunityPageViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

        ivBack.setOnClickListener {
            viewModel.onBackPressed()
        }
        cvBalanceDescription.visibility = View.VISIBLE
        viewModel.start()
    }

    override fun processViewCommand(command: ViewCommand) {
        super.processViewCommand(command)

        when(command) {
            is NavigateBackwardCommand -> requireFragmentManager().popBackStack()
            is SwitchToLeadsTabCommand -> switchToTab(1)
            is NavigateToMembersCommand -> navigateToMembers()
            is NavigateToFriendsCommand -> navigateToFriends(command.friends)
            is ShowCommunitySettings -> openCommunitySettingsDialog(command.communityPage!!,command.currentUserId)
            is NavigateToWalletConvertCommand -> navigateToWalletConvert(command.selectedCommunityId, command.balance)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        viewModel.communityPageLiveData.observe(viewLifecycleOwner, Observer {
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

            tvMemberCount.text = KiloCounterFormatter.format(it.membersCount)
            tvMembersLabel.text = resources.getQuantityString(R.plurals.plural_members, it.membersCount.toPluralInt())

            tvMemberCount.setOnClickListener { viewModel.onMembersLabelClick() }
            tvMembersLabel.setOnClickListener { viewModel.onMembersLabelClick() }

            if(it.friendsCount > 3) {
                tvFriendsCountLabel.visibility = View.VISIBLE
                tvFriendsLabel.visibility = View.VISIBLE

                tvFriendsCountLabel.text = getString(R.string.friends_label, KiloCounterFormatter.format(it.friendsCount-3))
                tvFriendsLabel.text = resources.getQuantityText(R.plurals.plural_friends, it.friendsCount.toPluralInt())

                tvFriendsCountLabel.setOnClickListener { viewModel.onFriendsLabelClick() }
                tvFriendsLabel.setOnClickListener { viewModel.onFriendsLabelClick() }
                communityFollowersView.setOnClickListener { viewModel.onFriendsLabelClick() }
            } else {
                tvFriendsCountLabel.visibility = View.GONE
                tvFriendsLabel.visibility = View.GONE
            }

            val communityCurrency = it.communityCurrency
            tvCurrentCurrency.text = communityCurrency.currencyName
            btnGetPoints.setOnClickListener { viewModel.onConvertClick() }
            tvCurrentCommunRate.text = communityCurrency.exchangeRate.toString()

            tvJoinTime.text = "${resources.getString(R.string.joined)} ${it.joinDate.toMMMM_DD_YYYY_Format()}"
            communityFollowersView.setFollowers(it.friends.take(FRIENDS_COUNT_MAX))
            ctvJoin.setOnClickListener { viewModel.changeJoinStatus() }
            ctvJoin.visibility = if(it.isInBlackList) View.GONE else View.VISIBLE
            initViewPager(it)
        })

        viewModel.rate.observe(viewLifecycleOwner, Observer {
            tvCurrentCommunRate.text = it.toString()
        })

        viewModel.communityPageIsErrorLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                NoConnectionPopup.show(this, root) { viewModel.loadCommunityPage() }
            } else {
                NoConnectionPopup.hide(this)
            }
        })

        viewModel.communityPageIsLoadProgressLiveData.observe(viewLifecycleOwner, Observer {
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

    private fun openCommunitySettingsDialog(communityPage:CommunityPage,currentUserId:String) {
        CommunitySettingsDialog.show(this,
            viewModel.communityPageLiveData.value?.isInBlackList?:false,
            viewModel.communityPageLiveData.value!!.name
        ){
            when(it){
                CommunitySettingsDialog.Result.HIDE_COMMUNITY->{viewModel.hideCommunity()}
                CommunitySettingsDialog.Result.SHARE_COMMUNITY->{
                    requireContext().shareMessage(viewModel.getShareString(communityPage,currentUserId))
                }
                CommunitySettingsDialog.Result.UNHIDE_COMMUNITY->{viewModel.unHideCommunity()}
            }
        }
    }

    override fun onDestroyView() {
        communityViewPagerRelease()
        super.onDestroyView()
    }

    private fun communityViewPagerRelease(){
        fragmentPagesList.clear()
        vpContent.adapter = null
    }

    private fun initViewPager(communityPage: CommunityPage) {
        tabLayout.apply {
            setupWithViewPager(vpContent)
            setSelectedTabIndicator(TabLineDrawable(requireContext()))
            setSelectedTabIndicatorColor(ContextCompat.getColor(requireContext(), R.color.blue))
        }

        fragmentPagesList = createPageFragmentsList(communityPage)
        vpContent.adapter = object : FragmentStatePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

            override fun getPageTitle(position: Int): CharSequence? {
                return tabTitles.getOrNull(position)
            }

            override fun getItem(position: Int): Fragment = fragmentPagesList[position]

            override fun getCount(): Int = fragmentPagesList.size

        }
        vpContent.offscreenPageLimit = fragmentPagesList.size
    }

    private fun createPageFragmentsList(communityPage: CommunityPage): MutableList<Fragment> {
        val fragmentPagesList = ArrayList<Fragment>()
        fragmentPagesList.add(
            CommunityPostFragment.newInstance(communityPage.communityId)
        )
        fragmentPagesList.add(
            LeadsListFragment.newInstance(arguments!!.getParcelable(ARG_COMMUNITY_ID)!!)
        )
        fragmentPagesList.add(
            CommunityPageAboutFragment.newInstance(communityPage.description)
        )
        fragmentPagesList.add(
            CommunityPageRulesFragment.newInstance(communityPage.rules)
        )
        return fragmentPagesList
    }

    private fun switchToTab(tabIndex: Int) = tabLayout.getTabAt(tabIndex)!!.select()

    private fun navigateToWalletConvert(selectedCommunityId: CommunityIdDomain, balance: List<WalletCommunityBalanceRecordDomain>) {
        getDashboardFragment(this)?.navigateToFragment(GetCommunityPointsFragment.newInstance(selectedCommunityId, balance))
    }

    private fun navigateToMembers() =
        getDashboardFragment(this)?.navigateToFragment(CommunityPageMembersFragment.newInstance(), true, null)

    private fun navigateToFriends(friends: List<CommunityFriend>) =
        getDashboardFragment(this)?.navigateToFragment(CommunityPageFriendsFragment.newInstance(friends), true, null)
}