package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.communities_fragment.my_community_fragment

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.main_activity.communities.tabs.my_community.view.MyCommunityFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [
    MyCommunityFragmentModuleBinds::class,
    MyCommunityFragmentModule::class
])
@SubFragmentScope
interface MyCommunityFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): MyCommunityFragmentComponent
    }

    fun inject(fragment: MyCommunityFragment)
}