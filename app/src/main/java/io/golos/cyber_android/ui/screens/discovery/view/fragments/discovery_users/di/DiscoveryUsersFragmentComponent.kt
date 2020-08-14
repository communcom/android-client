package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.view.DiscoveryUsersFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [DiscoveryUsersFragmentModuleBinds::class, DiscoveryUsersFragmentModule::class])
@SubFragmentScope
interface DiscoveryUsersFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: DiscoveryUsersFragmentModule): Builder
        fun build(): DiscoveryUsersFragmentComponent
    }

    fun inject(fragment: DiscoveryUsersFragment)
}