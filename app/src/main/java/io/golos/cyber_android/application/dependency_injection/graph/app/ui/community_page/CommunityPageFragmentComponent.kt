package io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_page.CommunityPageFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [CommunityPageFragmentModuleBinds::class])
@FragmentScope
interface CommunityPageFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): CommunityPageFragmentComponent
    }

    fun inject(fragment: CommunityPageFragment)
}