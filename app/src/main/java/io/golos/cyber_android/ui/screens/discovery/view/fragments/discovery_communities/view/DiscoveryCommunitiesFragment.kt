package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_communities.view

import android.os.Bundle
import io.golos.cyber_android.application.App
import io.golos.cyber_android.ui.screens.communities_list.view.CommunitiesListFragment
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_communities.di.DiscoveryCommunitiesFragmentComponent
import io.golos.domain.dto.UserIdDomain

open class DiscoveryCommunitiesFragment : CommunitiesListFragment() {
    companion object {

        private const val USER_ID = "USER_ID"

        fun newInstance(userId: UserIdDomain) = DiscoveryCommunitiesFragment().apply {
            arguments = Bundle().apply {
                putParcelable(USER_ID, userId)
            }
        }
    }

    override fun inject(key: String) {
        App.injections.get<DiscoveryCommunitiesFragmentComponent>(
            key,
            false,
            false,
            arguments!!.getParcelable(USER_ID),
            true
        ).inject(this)
    }

    override fun releaseInjection(key: String) {
        App.injections.release<DiscoveryCommunitiesFragmentComponent>(key)
    }
}