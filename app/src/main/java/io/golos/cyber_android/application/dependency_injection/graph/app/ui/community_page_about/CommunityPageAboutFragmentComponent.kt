package io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page_about

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_page.CommunityPageFragment
import io.golos.cyber_android.ui.screens.community_page_about.CommunityPageAboutFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [CommunityPageAboutFragmentModuleBinds::class])
@FragmentScope
interface CommunityPageAboutFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): CommunityPageAboutFragmentComponent
    }

    fun inject(fragment: CommunityPageAboutFragment)
}