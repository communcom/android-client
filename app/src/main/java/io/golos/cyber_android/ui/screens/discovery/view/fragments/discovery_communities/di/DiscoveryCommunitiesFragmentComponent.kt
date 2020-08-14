package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_communities.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discover_five_communities.di.DiscoveryFiveCommunitiesFragmentComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_communities.view.DiscoveryCommunitiesFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [DiscoveryCommunitiesFragmentModuleBinds::class,DiscoveryCommunitiesFragmentModule::class])
@FragmentScope
interface DiscoveryCommunitiesFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: DiscoveryCommunitiesFragmentModule): Builder
        fun build(): DiscoveryCommunitiesFragmentComponent
    }

    val discoveryFiveCommunitiesFragmentComponent: DiscoveryFiveCommunitiesFragmentComponent.Builder

    fun inject(fragment: DiscoveryCommunitiesFragment)
}