package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_followers

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.profile_followers.view.ProfileFollowersFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [ProfileFollowersFragmentModuleBinds::class, ProfileFollowersFragmentModule::class])
@SubFragmentScope
interface ProfileFollowersFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: ProfileFollowersFragmentModule): Builder
        fun build(): ProfileFollowersFragmentComponent
    }

    fun inject(fragment: ProfileFollowersFragment)
}