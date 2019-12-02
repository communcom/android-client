package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_list_fragment

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.communities_list.view.CommunitiesListFragmentTab
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [CommunitiesListFragmentModuleBinds::class, CommunitiesListFragmentModule::class])
@FragmentScope
interface CommunitiesListFragmentTabComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: CommunitiesListFragmentModule): Builder
        fun build(): CommunitiesListFragmentTabComponent
    }

    fun inject(fragment: CommunitiesListFragmentTab)
}