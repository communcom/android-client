package io.golos.cyber_android

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.golos.cyber_android.ui.screens.communities.CommunitiesFragment
import io.golos.cyber_android.ui.screens.feed.FeedFragment
import io.golos.cyber_android.ui.screens.notifications.NotificationsFragment
import io.golos.cyber_android.ui.screens.profile.ProfileFragment
import io.golos.cyber_android.ui.screens.wallet.WalletFragment
import io.golos.domain.interactors.model.CommunityId
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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

        ViewModelProviders.of(
            this,
            serviceLocator.getCommunityFeedViewModelFactory(CommunityId("gls"))
        )
            .get(CommunityFeedViewModel::class.java)



        setupPager()
        setupNavigationView()
    }

    private fun setupPager() {
        mainPager.isUserInputEnabled = false
        mainPager.adapter = object : FragmentStateAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (Tabs.values().find { it.index == position }) {
                    Tabs.FEED -> FeedFragment.newInstance()
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
