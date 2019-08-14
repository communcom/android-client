package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.discover_fragment

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.view.DiscoverFragment
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.discover.view.list.CommunityListItemViewHolder
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [
    DiscoverFragmentModuleBinds::class
])
@SubFragmentScope
interface DiscoverFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): DiscoverFragmentComponent
    }

    fun inject(fragment: DiscoverFragment)
    fun inject(viewHolder: CommunityListItemViewHolder)
}