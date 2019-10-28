package io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page_rules

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_page_rules.CommunityPageRulesFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(
    modules = [CommunityPageRulesFragmentModuleBinds::class,
        CommunityPageRulesFragmentModule::class]
)
@FragmentScope
interface CommunityPageRulesFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: CommunityPageRulesFragmentModule): Builder

        fun build(): CommunityPageRulesFragmentComponent
    }

    fun inject(fragment: CommunityPageRulesFragment)
}