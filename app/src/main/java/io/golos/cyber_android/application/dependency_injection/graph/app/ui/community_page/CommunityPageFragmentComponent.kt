package io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page

import dagger.Subcomponent
import io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page.leads_list_fragment.CommunityPageLeadsListComponent
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [CommunityPageFragmentModuleBinds::class, CommunityPageFragmentModuleChild::class])
@FragmentScope
interface CommunityPageFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): CommunityPageFragmentComponent
    }

    val leadsListFragment: CommunityPageLeadsListComponent.Builder

    fun inject(fragment: CommunityPageFragment)
}