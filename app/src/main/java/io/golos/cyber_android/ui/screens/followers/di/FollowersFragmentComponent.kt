package io.golos.cyber_android.ui.screens.followers.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.followers.FollowersFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [FollowersFragmentModuleBinds::class])
@FragmentScope
interface FollowersFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): FollowersFragmentComponent
    }

    fun inject(fragment: FollowersFragment)
}