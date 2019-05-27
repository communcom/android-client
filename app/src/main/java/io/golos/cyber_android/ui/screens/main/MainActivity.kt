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
import io.golos.cyber4j.model.CyberName
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.base.BaseActivity
import io.golos.cyber_android.ui.screens.feed.FeedFragment
import io.golos.cyber_android.ui.screens.notifications.NotificationsFragment
import io.golos.cyber_android.ui.screens.profile.ProfileFragment
import io.golos.cyber_android.utils.asEvent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_notification_badge.*


class MainActivity : BaseActivity() {

    enum class Tabs(val index: Int, @IdRes val navItem: Int) {
        FEED(0, R.id.navigation_feed),
        //COMMUNITIES(1, R.id.navigation_communities),
        NOTIFICATIONS(1, R.id.navigation_notifications),
        //WALLET(3, R.id.navigation_wallet),
        PROFILE(2, R.id.navigation_profile)
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addNotificationsBadge()
        setupViewModel()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.unreadNotificationsLiveData.observe(this, Observer {
            refreshNotificationsBadge(it)
        })

        viewModel.authStateLiveData.asEvent().observe(this, Observer { authState ->
            authState.getIfNotHandled()?.let {
                setupPager(it.userName)
                setupNavigationView()
            }
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
        notificationsBadge?.visibility = if (count == 0) View.GONE else View.VISIBLE
        updatesCount?.text = if (count > 99) "99" else count.toString()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            serviceLocator
                .getDefaultViewModelFactory()
        ).get(MainViewModel::class.java)
    }

    private fun setupPager(user: CyberName) {
        mainPager.isUserInputEnabled = false
        mainPager.offscreenPageLimit = Tabs.values().size
        mainPager.adapter = object : FragmentStateAdapter(supportFragmentManager, this.lifecycle) {
            override fun getItem(position: Int): Fragment {
                return when (Tabs.values().find { it.index == position }) {
                    Tabs.FEED -> FeedFragment.newInstance("gls", user.name)
                    //Tabs.COMMUNITIES -> CommunitiesFragment.newInstance()
                    Tabs.NOTIFICATIONS -> NotificationsFragment.newInstance()
                    //Tabs.WALLET -> WalletFragment.newInstance()
                    Tabs.PROFILE -> ProfileFragment.newInstance(user.name)
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
