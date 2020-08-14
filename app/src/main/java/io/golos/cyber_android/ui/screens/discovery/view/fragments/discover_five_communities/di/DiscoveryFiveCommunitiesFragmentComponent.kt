package io.golos.cyber_android.ui.screens.discovery.view.fragments.discover_five_communities.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discover_five_communities.view.DiscoveryFiveCommunitiesFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [DiscoveryFiveCommunitiesFragmentModuleBinds::class, DiscoveryFiveCommunitiesFragmentModule::class])
@SubFragmentScope
interface DiscoveryFiveCommunitiesFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: DiscoveryFiveCommunitiesFragmentModule): Builder
        fun build(): DiscoveryFiveCommunitiesFragmentComponent
    }

    fun inject(fragment: DiscoveryFiveCommunitiesFragment)
}