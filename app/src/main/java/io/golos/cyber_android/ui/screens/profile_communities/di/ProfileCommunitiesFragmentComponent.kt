package io.golos.cyber_android.ui.screens.profile_communities.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.profile_communities.view.ProfileCommunitiesFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [ProfileCommunitiesFragmentModuleBinds::class, ProfileCommunitiesFragmentModule::class])
@SubFragmentScope
interface ProfileCommunitiesFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: ProfileCommunitiesFragmentModule): Builder
        fun build(): ProfileCommunitiesFragmentComponent
    }

    fun inject(fragment: ProfileCommunitiesFragment)
}