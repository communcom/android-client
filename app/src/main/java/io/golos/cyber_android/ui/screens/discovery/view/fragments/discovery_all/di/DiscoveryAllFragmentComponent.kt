package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.view.DiscoveryAllFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [DiscoveryAllFragmentModuleBinds::class, DiscoveryAllFragmentModule::class])
@SubFragmentScope
interface DiscoveryAllFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: DiscoveryAllFragmentModule): Builder
        fun build(): DiscoveryAllFragmentComponent
    }

    fun inject(fragment: DiscoveryAllFragment)
}