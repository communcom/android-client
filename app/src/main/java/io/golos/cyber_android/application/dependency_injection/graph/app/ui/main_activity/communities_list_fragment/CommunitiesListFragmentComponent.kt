package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_list_fragment

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