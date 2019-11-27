package io.golos.cyber_android.ui.screens.main_activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.cyber_android.R
import io.golos.cyber_android.application.App
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.MainActivityComponent
import io.golos.cyber_android.ui.common.base.ActivityBase
import io.golos.cyber_android.ui.common.mvvm.viewModel.ActivityViewModelFactory
import io.golos.cyber_android.ui.common.widgets.NavigationBottomMenuWidget
import io.golos.cyber_android.ui.screens.editor_page_activity.EditorPageActivity
import io.golos.cyber_android.ui.screens.feed.FeedFragment
import io.golos.cyber_android.ui.screens.main_activity.communities.view.CommunitiesFragment
import io.golos.cyber_android.ui.screens.main_activity.notifications.NotificationsFragment
import io.golos.cyber_android.ui.screens.profile.new_profile.view.ProfileFragment
import io.golos.cyber_android.ui.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_notification_badge.*
import javax.inject.Inject


class MainActivity : ActivityBase() {

    private lateinit var viewModel: MainViewModel

    @Inject
    internal lateinit var viewModelFactory: ActivityViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        App.injections.get<MainActivityComponent>().inject(this)

        addNotificationsBadge()
        setupViewModel()
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
            startActivity(EditorPageActivity.getIntent(this))
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

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }

    private fun setupPager(user: CyberName) {
        mainPager.isUserInputEnabled = false
        mainPager.offscreenPageLimit = NavigationBottomMenuWidget.Tab.values().size
        mainPager.adapter = object : FragmentStateAdapter(supportFragmentManager, this.lifecycle) {
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
                        CommunitiesFragment.newInstance()
                    }
                    NavigationBottomMenuWidget.Tab.NOTIFICATIONS -> {
                        NotificationsFragment.newInstance()
                    }
                    NavigationBottomMenuWidget.Tab.PROFILE -> {
                        ProfileFragment.newInstance(user.name)
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
                        setStatusBarColor(R.color.feed_status_bar_color)
                        tintStatusBarIcons(true)
                    }
                    NavigationBottomMenuWidget.Tab.COMMUNITIES.index -> {
                        setStatusBarColor(R.color.window_status_bar_background)
                        tintStatusBarIcons(true)
                    }
                    NavigationBottomMenuWidget.Tab.PROFILE.index -> {
                        setStatusBarColor(R.color.window_status_bar_background)
                        tintStatusBarIcons(true)
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showFragment(fragment: Fragment, isAddToBackStack: Boolean = true) {
        val tag = fragment::class.simpleName
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            val beginTransaction = supportFragmentManager.beginTransaction()
            if (isAddToBackStack) {
                beginTransaction.addToBackStack(tag)
            }
            beginTransaction
                .add(R.id.rootContainer, fragment, tag)
                .commit()
        }
    }
}
