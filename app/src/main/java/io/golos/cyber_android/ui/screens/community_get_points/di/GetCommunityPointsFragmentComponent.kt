package io.golos.cyber_android.ui.screens.community_get_points.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_get_points.GetCommunityPointsFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [GetCommunityPointsFragmentModuleBinds::class, GetCommunityPointsFragmentModule::class])
@FragmentScope
interface GetCommunityPointsFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: GetCommunityPointsFragmentModule): Builder
        fun build(): GetCommunityPointsFragmentComponent
    }

    fun inject(fragment: GetCommunityPointsFragment)
}