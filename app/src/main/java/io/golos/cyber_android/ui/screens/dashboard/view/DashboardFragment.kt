package io.golos.cyber_android.ui.screens.dashboard.view

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.databinding.FragmentDashboardBinding
import io.golos.cyber_android.ui.common.mvvm.FragmentBaseMVVM
import io.golos.cyber_android.ui.common.widgets.NavigationBottomMenuWidget
import io.golos.cyber_android.ui.screens.communities_list.view.CommunitiesListFragmentTab
import io.golos.cyber_android.ui.screens.dashboard.di.DashboardFragmentComponent
import io.golos.cyber_android.ui.screens.dashboard.view_model.DashboardViewModel
import io.golos.cyber_android.ui.screens.editor_page_activity.EditorPageActivity
import io.golos.cyber_android.ui.screens.feed.FeedFragment
import io.golos.cyber_android.ui.screens.main_activity.notifications.NotificationsFragment
import io.golos.cyber_android.ui.screens.profile.view.ProfileFragment
import io.golos.cyber_android.ui.utils.*
import io.golos.domain.dto.UserIdDomain
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.view_notification_badge.*

class DashboardFragment : FragmentBaseMVVM<FragmentDashboardBinding, DashboardViewModel>() {

    override fun provideViewModelType(): Class<DashboardViewModel> = DashboardViewModel::class.java

    override fun layoutResId(): Int = R.layout.fragment_dashboard

    override fun inject() = App.injections.get<DashboardFragmentComponent>()
        .inject(this)


    override fun releaseInjection() {
        App.injections.release<DashboardFragmentComponent>()
    }

    override fun linkViewModel(binding: FragmentDashboardBinding, viewModel: DashboardViewModel) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addNotificationsBadge()
        observeViewModel()
        navigationMenu.clickListener = viewModel
    }

    private fun observeViewModel() {
        viewModel.unreadNotificationsLiveData.observe(this, Observer {
            refreshNotificationsBadge(it)
        })

        viewModel.authStateLiveData.asEvent().observe(this, Observer { authState ->
            authState.getIfNotHandled()?.let {
                setupPager(it.userName)
            }
        })

        viewModel.getCurrentTabLiveData.observe(this, Observer {
            val tab =
                NavigationBottomMenuWidget.Tab.values().find { navigationTab ->
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
            mainPager.currentItem = it.index
        })

        viewModel.createTabLiveData.observe(this, Observer {
            startActivity(EditorPageActivity.getIntent(requireContext()))
        })
    }

    private var notificationsBadge: View? = null

    private fun addNotificationsBadge() {
//        val menuView = navigationView.getChildAt(0) as BottomNavigationMenuView
//        val itemView = menuView.getChildAt(Tab.NOTIFICATIONS.index) as BottomNavigationItemView
//        notificationsBadge = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, menuView, false)
//        notificationsBadge?.visibility = View.GONE
//        itemView.addView(notificationsBadge)
    }

    private fun refreshNotificationsBadge(count: Int) {
        notificationsBadge?.visibility = if (count == 0) View.GONE else View.VISIBLE
        updatesCount?.text = if (count > 99) "99" else count.toString()
    }

    private fun setupPager(user: CyberName) {
        mainPager.isUserInputEnabled = false
        mainPager.offscreenPageLimit = NavigationBottomMenuWidget.Tab.values().size
        mainPager.adapter = object : FragmentStateAdapter(childFragmentManager, this.lifecycle) {
            override fun createFragment(position: Int): Fragment {
                val tab =
                    NavigationBottomMenuWidget.Tab.values().find { navigationTab ->
                        navigationTab.index == position
                    }
                return when (tab) {
                    NavigationBottomMenuWidget.Tab.FEED -> {
                        FeedFragment.newInstance("gls", user.name)
                    }
                    NavigationBottomMenuWidget.Tab.COMMUNITIES -> {
                        CommunitiesListFragmentTab.newInstance(UserIdDomain(user.name))
                    }
                    NavigationBottomMenuWidget.Tab.NOTIFICATIONS -> {
                        NotificationsFragment.newInstance()
                    }
                    NavigationBottomMenuWidget.Tab.PROFILE -> {
                        ProfileFragment.newInstance(UserIdDomain(user.name))
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
                        requireActivity().setStatusBarColor(R.color.feed_status_bar_color)
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
        val tag = tagFragment ?: fragment::class.simpleName
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