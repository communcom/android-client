package io.golos.cyber_android.ui.screens.profile_followers.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.profile_followers.view.ProfileFollowersExternalUserFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [ProfileFollowersFragmentModuleBinds::class, ProfileFollowersFragmentModule::class])
@SubFragmentScope
interface ProfileFollowersExternalUserFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: ProfileFollowersFragmentModule): Builder
        fun build(): ProfileFollowersExternalUserFragmentComponent
    }

    fun inject(fragment: ProfileFollowersExternalUserFragment)
}