package io.golos.cyber_android.ui.screens.community_page.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_page_leaders_list.di.CommunityPageLeadsListComponent
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.cyber_android.ui.screens.community_page_members.di.CommunityPageMembersComponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [CommunityPageFragmentModuleBinds::class, CommunityPageFragmentModuleChild::class])
@FragmentScope
interface CommunityPageFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): CommunityPageFragmentComponent
    }

    val leadsListFragment: CommunityPageLeadsListComponent.Builder
    val membersFragment: CommunityPageMembersComponent.Builder

    fun inject(fragment: CommunityPageFragment)
}