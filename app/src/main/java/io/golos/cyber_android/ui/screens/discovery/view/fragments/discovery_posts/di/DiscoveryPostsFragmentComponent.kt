package io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.discovery.di.DiscoveryFragmentModule
import io.golos.cyber_android.ui.screens.discovery.di.DiscoveryFragmentModuleBinds
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_all.di.DiscoveryAllFragmentComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_posts.DiscoveryPostsFragment
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.view.DiscoveryUsersFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope
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