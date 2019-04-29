package io.golos.cyber_android.ui.screens.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.base.BaseActivity
import io.golos.cyber_android.ui.screens.communities.CommunitiesFragment
import io.golos.cyber_android.ui.screens.feed.FeedFragment
import io.golos.cyber_android.ui.screens.notifications.NotificationsFragment
import io.golos.cyber_android.ui.screens.profile.ProfileFragment
import io.golos.cyber_android.ui.screens.wallet.WalletFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_notification_badge.*


class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainActivityViewModel


    enum class Tabs(val index: Int, @IdRes val navItem: Int) {
        FEED(0, R.id.navigation_feed),
        COMMUNITIES(1, R.id.navigation_communities),
        NOTIFICATIONS(2, R.id.navigation_notifications),
        WALLET(3, R.id.navigation_wallet),
        PROFILE(4, R.id.navigation_profile)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPager()
        setupNavigationView()
        setupViewModel()
        observeViewModel()

        addNotificationsBadge()
    }

    private fun observeViewModel() {
        viewModel.unreadNotificationsLiveData.observe(this, Observer {
            refreshNotificationsBadge(it)
        })
    }

    private var notificationsBadge: View? = null

    private fun addNotificationsBadge() {
        val menuView = navigationView.getChildAt(0) as BottomNavigationMenuView
        val itemView = menuView.getChildAt(Tabs.NOTIFICATIONS.index) as BottomNavigationItemView
        notificationsBadge = LayoutInflater.from(this).inflate(R.layout.view_notification_badge, menuView, false)
        notificationsBadge?.visibility = View.GONE
        itemView.addView(notificationsBadge)
    }

    private fun refreshNotificationsBadge(count: Int) {
        //notificationsBadge?.visibility = View.VISIBLE
        notificationsBadge?.visibility = if (count == 0) View.GONE else View.VISIBLE
        updatesCount?.text = if (count > 99) "99" else count.toString()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            serviceLocator
                .getMainActivityViewModelFactory()
        ).get(MainActivityViewModel::class.java)
    }

    private fun setupPager() {
        mainPager.isUserInputEnabled = false
        mainPager.adapter = object : FragmentStateAdapter(supportFragmentManager, this.lifecycle) {
            override fun getItem(position: Int): Fragment {
                return when (Tabs.values().find { it.index == position }) {
                    Tabs.FEED -> FeedFragment.newInstance("gls", "destroyer2k")
                    Tabs.COMMUNITIES -> CommunitiesFragment.newInstance()
                    Tabs.NOTIFICATIONS -> NotificationsFragment.newInstance()
                    Tabs.WALLET -> WalletFragment.newInstance()
                    Tabs.PROFILE -> ProfileFragment.newInstance()
                    null -> throw IndexOutOfBoundsException("page index is not in supported tabs range")
                }
            }

            override fun getItemCount() = Tabs.values().size
        }
    }

    private fun setupNavigationView() {
        navigationView.setOnNavigationItemSelectedListener {
            val pageIndex = Tabs.values().find { tab -> it.itemId == tab.navItem }?.index
            if (pageIndex != null)
                mainPager.setCurrentItem(pageIndex, false)
            return@setOnNavigationItemSelectedListener true
        }
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
}
