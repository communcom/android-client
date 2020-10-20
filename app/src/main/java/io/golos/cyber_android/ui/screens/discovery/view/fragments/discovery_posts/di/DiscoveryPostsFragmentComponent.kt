package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts.DiscoveryPostsFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [DiscoveryPostsFragmentModuleBinds::class, DiscoveryPostsFragmentModule::class])
@SubFragmentScope
interface DiscoveryPostsFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: DiscoveryPostsFragmentModule): Builder
        fun build(): DiscoveryPostsFragmentComponent
    }

    fun inject(fragment: DiscoveryPostsFragment)
}