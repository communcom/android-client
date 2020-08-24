package io.golos.cyber_android.ui.screens.discovery.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.discovery.view.DiscoveryFragmentTab
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discover_five_communities.di.DiscoveryFiveCommunitiesFragmentComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.di.DiscoveryAllFragmentComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts.di.DiscoveryPostsFragmentComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.di.DiscoveryUsersForFiveItemsComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.di.DiscoveryUsersFragmentComponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    DiscoveryFragmentModuleBinds::class,
    DiscoveryFragmentModule::class,
    DiscoveryFragmentComponentChilds::class
])
@FragmentScope
interface DiscoveryFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: DiscoveryFragmentModule): Builder
        fun build(): DiscoveryFragmentComponent
    }

    val discoveryUsersComponent: DiscoveryUsersFragmentComponent.Builder
    val discoveryUsersForFiveItemsComponent:DiscoveryUsersForFiveItemsComponent.Builder
    val discoveryPostsFragmentComponent: DiscoveryPostsFragmentComponent.Builder
    val discoveryAllFragmentComponent: DiscoveryAllFragmentComponent.Builder
    val discoveryFiveCommunitiesFragmentComponent: DiscoveryFiveCommunitiesFragmentComponent.Builder

    fun inject(fragment: DiscoveryFragmentTab)
}