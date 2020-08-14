package io.golos.cyber_android.ui.screens.discovery.view.fragments.discover_five_communities.view

import android.os.Bundle
import android.util.Log
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.communities_list.view.CommunitiesListFragment
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discover_five_communities.di.DiscoveryFiveCommunitiesFragmentComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.di.DiscoveryUsersForFiveItemsComponent
import io.golos.domain.dto.UserIdDomain

open class DiscoveryFiveCommunitiesFragment : CommunitiesListFragment() {
    companion object {

        private const val USER_ID = "USER_ID"

        fun newInstance(userId: UserIdDomain) = DiscoveryFiveCommunitiesFragment().apply {
            arguments = Bundle().apply {
                putParcelable(USER_ID, userId)
            }
        }
    }

    override fun inject(key: String) = App.injections.get<DiscoveryFiveCommunitiesFragmentComponent>(key, false, false, true, 5, true).inject(this)

    override fun releaseInjection(key: String) {
        App.injections.release<DiscoveryFiveCommunitiesFragmentComponent>(key)
    }
}