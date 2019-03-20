package io.golos.cyber_android.views.utils

import com.google.android.material.tabs.TabLayout

/**
 * Simple [TabLayout.OnTabSelectedListener] that allows to implement only necessary methods
 */
open class BaseOnTabSelectedListener : TabLayout.OnTabSelectedListener {
    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
    }
}