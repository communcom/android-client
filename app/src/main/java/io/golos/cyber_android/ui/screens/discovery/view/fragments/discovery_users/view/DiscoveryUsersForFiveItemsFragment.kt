package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.view

import android.os.Bundle
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_communities.view.DiscoveryCommunitiesFragment
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.di.DiscoveryUsersForFiveItemsComponent
import io.golos.domain.dto.UserIdDomain

class DiscoveryUsersForFiveItemsFragment : DiscoveryUsersFragment() {
    companion object {

        private const val USER_ID = "USER_ID"

        fun newInstance(userId: UserIdDomain) = DiscoveryCommunitiesFragment().apply {
            arguments = Bundle().apply {
                putParcelable(USER_ID, userId)
            }
        }
    }

    override fun inject(key: String) = App.injections.get<DiscoveryUsersForFiveItemsComponent>(key, 5, true).inject(this)

    override fun releaseInjection(key: String) = App.injections.release<DiscoveryUsersForFiveItemsComponent>(key)

}