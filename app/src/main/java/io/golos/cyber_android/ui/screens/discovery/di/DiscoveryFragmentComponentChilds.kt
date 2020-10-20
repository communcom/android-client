package io.golos.cyber_android.ui.screens.discovery.di

import dagger.Module
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discover_five_communities.di.DiscoveryFiveCommunitiesFragmentComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.di.DiscoveryAllFragmentComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts.di.DiscoveryPostsFragmentComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.di.DiscoveryUsersForFiveItemsComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.di.DiscoveryUsersFragmentComponent

@Module(subcomponents = [
    DiscoveryAllFragmentComponent::class,
    DiscoveryUsersFragmentComponent::class,
    DiscoveryUsersForFiveItemsComponent::class,
    DiscoveryFiveCommunitiesFragmentComponent::class,
    DiscoveryPostsFragmentComponent::class
])
class DiscoveryFragmentComponentChilds