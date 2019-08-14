package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment

import dagger.Subcomponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.discover_fragment.DiscoverFragmentComponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [
    CommunitiesFragmentModuleChilds::class,
    CommunitiesFragmentModuleBinds::class
])
@FragmentScope
interface CommunitiesFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): CommunitiesFragmentComponent
    }

    val discoverFragmentComponent: DiscoverFragmentComponent.Builder
}