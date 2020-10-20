package io.golos.cyber_android.ui.screens.communities_list.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.communities_list.view.CommunitiesListFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [CommunitiesListFragmentModuleBinds::class, CommunitiesListFragmentModule::class])
@FragmentScope
interface CommunitiesListFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: CommunitiesListFragmentModule): Builder
        fun build(): CommunitiesListFragmentComponent
    }

    fun inject(fragment: CommunitiesListFragment)
}