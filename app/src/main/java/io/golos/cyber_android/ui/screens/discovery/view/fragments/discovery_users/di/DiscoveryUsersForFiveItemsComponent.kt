package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.view.DiscoveryUsersForFiveItemsFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [DiscoveryUsersFragmentModuleBinds::class, DiscoveryUsersFragmentModule::class])
@SubFragmentScope
interface DiscoveryUsersForFiveItemsComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: DiscoveryUsersFragmentModule): Builder
        fun build(): DiscoveryUsersForFiveItemsComponent
    }

    fun inject(fragment: DiscoveryUsersForFiveItemsFragment)
}