package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.main_activity.communities.view.CommunitiesFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [CommunitiesFragmentModuleBinds::class])
@FragmentScope
interface CommunitiesFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): CommunitiesFragmentComponent
    }

    fun inject(fragment: CommunitiesFragment)
}