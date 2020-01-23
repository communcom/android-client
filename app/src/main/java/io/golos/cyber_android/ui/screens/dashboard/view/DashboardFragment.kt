package io.golos.cyber_android.ui.screens.dashboard.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentDashboardBinding
import io.golos.cyber_android.ui.dto.ContentId
import io.golos.cyber_android.ui.screens.communities_list.view.CommunitiesListFragmentTab
import io.golos.cyber_android.ui.screens.dashboard.di.DashboardFragmentComponent
import io.golos.cyber_android.ui.screens.dashboard.view_model.DashboardViewModel
import io.golos.cyber_android.ui.screens.feed.FeedFragment
import io.golos.cyber_android.ui.screens.notifications.NotificationsFragment
import io.golos.cyber_android.ui.screens.post_edit.activity.EditorPageActivity
import io.golos.cyber_android.ui.screens.post_view.view.PostPageFragment
import io.golos.cyber_android.ui.screens.profile.view.ProfileFragment
import io.golos.cyber_android.ui.shared.Tags
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.utils.dp
import io.golos.cyber_android.ui.shared.utils.setBottomMargin
import io.golos.cyber_android.ui.shared.utils.setStatusBarColor
import io.golos.cyber_android.ui.shared.utils.tintStatusBarIcons
import io.golos.cyber_android.ui.shared.widgets.NavigationBottomMenuWidget
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : FragmentBaseMVVM<FragmentDashboardBinding, DashboardViewModel>() {

    companion object {
        private const val REQUEST_FOR_RESULT_FROM_EDIT = 41522
    }

    override fun provideViewModelType(): Class<DashboardViewModel> = DashboardViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_dashboard

    override fun inject(key: String) = App.injections.get<DashboardFragmentComponent>(key).inject(this)


    override fun releaseInjection(key: String) = App.injections.release<DashboardFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentDashboardBinding, viewModel: DashboardViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPager(viewModel.currentUser)

        addNotificationsBadge()
        observeViewModel()
        navigationMenu.clickListener = viewModel
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_FOR_RESULT_FROM_EDIT -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        data?.action?.let { action ->
                            when (action) {
                                Tags.ACTION_EDIT_SUCCESS -> {
                                    val contentId = data.getParcelableExtra<ContentId>(Tags.CONTENT_ID)
                                    val discussionIdModel = DiscussionIdModel(
                                        contentId.userId,
                                        Permlink(contentId.permlink)
                                    )
                                    openPost(discussionIdModel, contentId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openPost(
        discussionIdModel: DiscussionIdModel,
        contentId: ContentId
    ) {
        showFragment(
            PostPageFragment.newInstance(
                PostPageFragment.Args(
                    discussionIdModel,
                    contentId
                )
            ),
            tagFragment = contentId.permlink
        )
    }

    private fun observeViewModel() {
        viewModel.getCurrentTabLiveData.observe(viewLifecycleOwner, Observer {
            val tab = NavigationBottomMenuWidget.Tab.values().find { navigationTab ->
                navigationTab.index == it.index
            }

            when (tab) {
                NavigationBottomMenuWidget.Tab.FEED -> {
                    (mainPager.layoutParams as FrameLayout.LayoutParams).setBottomMargin(70.dp)
                }
                NavigationBottomMenuWidget.Tab.COMMUNITIES -> {
                    (mainPager.layoutParams as FrameLayout.LayoutParams).setBottomMargin(70.dp)
                }
                NavigationBottomMenuWidget.Tab.PROFILE -> {
                    (mainPager.layoutParams as FrameLayout.LayoutParams).setBottomMargin(45.dp)
                }
                null -> (mainPager.layoutParams as FrameLayout.LayoutParams).setBottomMargin(0)
            }
            mainPager.setCurrentItem(it.index, false)
        })

        viewModel.createTabLiveData.observe(viewLifecycleOwner, Observer {
            startActivityForResult(
                EditorPageActivity.getIntent(requireContext()),
                REQUEST_FOR_RESULT_FROM_EDIT
            )
        })
    }

    private fun addNotificationsBadge() {
//        val menuView = navigationView.getChildAt(0) as BottomNavigationMenuView
//        val itemView = menuView.getChildAt(Tab.NOTIFICATIONS.index) as BottomNavigationItemView
//        notificationsBadge = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, menuView, false)
//        notificationsBadge?.visibility = View.GONE
//        itemView.addView(notificationsBadge)
    }

    private fun setupPager(user: UserIdDomain) {
        mainPager.isUserInputEnabled = false
        mainPager.offscreenPageLimit = NavigationBottomMenuWidget.Tab.values().size - 1

        mainPager.adapter = object : FragmentStateAdapter(childFragmentManager, this.lifecycle) {
            override fun createFragment(position: Int): Fragment {
                val tab = NavigationBottomMenuWidget.Tab.values().find { navigationTab ->
                    navigationTab.index == position
                }
                return when (tab) {
                    NavigationBottomMenuWidget.Tab.FEED -> {
                        FeedFragment.newInstance("gls", user.userId)
                    }
                    NavigationBottomMenuWidget.Tab.COMMUNITIES -> {
                        CommunitiesListFragmentTab.newInstance(UserIdDomain(user.userId))
                    }
                    NavigationBottomMenuWidget.Tab.NOTIFICATIONS -> {
                        NotificationsFragment.newInstance()
                    }
                    NavigationBottomMenuWidget.Tab.PROFILE -> {
                        ProfileFragment.newInstance(UserIdDomain(user.userId))
                    }
                    null -> throw IndexOutOfBoundsException("page index is not in supported tabs range")
                }
            }

            override fun getItemCount() = NavigationBottomMenuWidget.Tab.values().size
        }

        mainPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    NavigationBottomMenuWidget.Tab.FEED.index -> {
                        requireActivity().setStatusBarColor(R.color.window_status_bar_background)
                        requireActivity().tintStatusBarIcons(true)
                    }
                    NavigationBottomMenuWidget.Tab.COMMUNITIES.index -> {
                        requireActivity().setStatusBarColor(R.color.window_status_bar_background)
                        requireActivity().tintStatusBarIcons(true)
                    }
                    NavigationBottomMenuWidget.Tab.PROFILE.index -> {
                        requireActivity().setStatusBarColor(R.color.window_status_bar_background)
                        requireActivity().tintStatusBarIcons(true)
                    }
                }
            }
        })
    }

    fun showFragment(fragment: Fragment, isAddToBackStack: Boolean = true, tagFragment: String? = null) {
        val tag = tagFragment ?: "${fragment::class.simpleName}_${fragment.hashCode()}"
        if (childFragmentManager.findFragmentByTag(tag) == null) {
            val beginTransaction = childFragmentManager.beginTransaction()
            if (isAddToBackStack) {
                beginTransaction.addToBackStack(tag)
            }

            beginTransaction.setCustomAnimations(
                R.anim.nav_slide_in_right,
                R.anim.nav_slide_out_left,
                R.anim.nav_slide_in_left,
                R.anim.nav_slide_out_right
            )

            beginTransaction
                .add(R.id.rootContainer, fragment, tag)
                .commit()
        }
    }
}