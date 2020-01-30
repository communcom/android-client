package io.golos.cyber_android.ui.screens.dashboard.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import io.golos.cyber_android.ui.screens.notifications.view.NotificationsFragment
import io.golos.cyber_android.ui.screens.post_edit.activity.EditorPageActivity
import io.golos.cyber_android.ui.screens.post_view.view.PostPageFragment
import io.golos.cyber_android.ui.screens.profile.view.ProfileFragment
import io.golos.cyber_android.ui.shared.Tags
import io.golos.cyber_android.ui.shared.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.shared.utils.setStatusBarColor
import io.golos.cyber_android.ui.shared.widgets.NavigationBottomMenuWidget
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.DiscussionIdModel
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : FragmentBaseMVVM<FragmentDashboardBinding, DashboardViewModel>() {

    private companion object {
        private const val REQUEST_FOR_RESULT_FROM_EDIT = 41522
    }

    private val viewPagerFragmentsList = mutableListOf<Fragment>()

    override fun provideViewModelType(): Class<DashboardViewModel> = DashboardViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_dashboard

    override fun inject(key: String) = App.injections.get<DashboardFragmentComponent>(key).inject(this)


    override fun releaseInjection(key: String) = App.injections.release<DashboardFragmentComponent>(key)

    override fun linkViewModel(binding: FragmentDashboardBinding, viewModel: DashboardViewModel) {
        binding.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updateUnreadNotificationsCounter()
        childFragmentManager.addOnBackStackChangedListener {
            val backStackEntryCount = childFragmentManager.backStackEntryCount
            if(backStackEntryCount == 0){
                //In DashboardFragment
                handleNavigationTabPosition(mainPager.currentItem, true)
            } else{
                handleNavigationTabPosition(-1, true)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPager(viewModel.currentUser)
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
        viewModel.currentTabLiveData.observe(viewLifecycleOwner, Observer {
            val tab = NavigationBottomMenuWidget.Tab.values().find { navigationTab ->
                navigationTab.index == it.index
            }
            mainPager.setCurrentItem(it.index, false)
        })

        viewModel.createTabLiveData.observe(viewLifecycleOwner, Observer {
            startActivityForResult(
                EditorPageActivity.getIntent(requireContext()),
                REQUEST_FOR_RESULT_FROM_EDIT
            )
        })

        viewModel.newNotificationsCounter.observe(viewLifecycleOwner, Observer {
            navigationMenu.setNotificationsCounter(it)
        })
    }

    private fun handleNavigationTabPosition(position: Int, changeStackPage: Boolean) {
        val currentActivity = requireActivity()
        val notificationPageIndex = NavigationBottomMenuWidget.Tab.NOTIFICATIONS.index
        val notificationsFragment = viewPagerFragmentsList[notificationPageIndex] as NotificationsFragment
        when (position) {
            NavigationBottomMenuWidget.Tab.FEED.index -> {
                currentActivity.setStatusBarColor(R.color.window_status_bar_second_background)
                notificationsFragment.onVisibilityChanged(visible = false, changeStackPage = changeStackPage)
            }
            NavigationBottomMenuWidget.Tab.COMMUNITIES.index -> {
                currentActivity.setStatusBarColor(R.color.window_status_bar_background)
                notificationsFragment.onVisibilityChanged(visible = false, changeStackPage = changeStackPage)
            }
            NavigationBottomMenuWidget.Tab.PROFILE.index -> {
                currentActivity.setStatusBarColor(R.color.window_status_bar_background)
                notificationsFragment.onVisibilityChanged(visible = false, changeStackPage = changeStackPage)
            }
            notificationPageIndex -> {
                currentActivity.setStatusBarColor(R.color.window_status_bar_background)
                notificationsFragment.onVisibilityChanged(visible = true, changeStackPage = changeStackPage)
            }
            else -> {
                currentActivity.setStatusBarColor(R.color.window_status_bar_background)
                notificationsFragment.onVisibilityChanged(visible = false, changeStackPage = changeStackPage)
            }
        }
    }

    private fun getCurrentTab(): NavigationBottomMenuWidget.Tab? {
        return NavigationBottomMenuWidget.Tab.values().find { navigationTab ->
            navigationTab.index == mainPager.currentItem
        }
    }

    private fun setupPager(user: UserIdDomain) {
        mainPager.isUserInputEnabled = false
        mainPager.offscreenPageLimit = NavigationBottomMenuWidget.Tab.values().size - 1
        viewPagerFragmentsList.apply {
            add(NavigationBottomMenuWidget.Tab.FEED.index, FeedFragment.newInstance("gls", user.userId))
            add(NavigationBottomMenuWidget.Tab.COMMUNITIES.index, CommunitiesListFragmentTab.newInstance(UserIdDomain(user.userId)))
            add(NavigationBottomMenuWidget.Tab.NOTIFICATIONS.index, NotificationsFragment.newInstance())
            add(NavigationBottomMenuWidget.Tab.PROFILE.index, ProfileFragment.newInstance(UserIdDomain(user.userId)))
        }
        mainPager.adapter = object : FragmentStateAdapter(childFragmentManager, this.lifecycle) {
            override fun createFragment(position: Int): Fragment {
                val tab = NavigationBottomMenuWidget.Tab.values().find { navigationTab ->
                    navigationTab.index == position
                }
                return tab?.index?.let {
                    viewPagerFragmentsList.get(it)
                } ?: throw IndexOutOfBoundsException("page index is not in supported tabs range")
            }

            override fun getItemCount() = NavigationBottomMenuWidget.Tab.values().size
        }

        mainPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handleNavigationTabPosition(position, false)
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