package io.golos.cyber_android.ui.screens.community_page.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_page.view.CommunityPageFragment
import io.golos.cyber_android.ui.screens.community_page_friends.di.CommunityPageFriendsComponent
import io.golos.cyber_android.ui.screens.community_page_leaders_list.di.CommunityPageLeadsListComponent
import io.golos.cyber_android.ui.screens.community_page_members.di.CommunityPageMembersComponent
import io.golos.domain.dependency_injection.scopes.FragmentScope
import io.golos.cyber_android.ui.screens.community_page_proposals.di.CommunityProposalsFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_reports.di.CommunityReportsFragmentComponent

@Subcomponent(modules = [
    CommunityPageFragmentModuleBinds::class,
    CommunityPageFragmentModule::class,
    CommunityPageFragmentModuleChild::class])
@FragmentScope
interface CommunityPageFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: CommunityPageFragmentModule): Builder
        fun build(): CommunityPageFragmentComponent
    }

    val leadsListFragment: CommunityPageLeadsListComponent.Builder
    val membersFragment: CommunityPageMembersComponent.Builder
    val friendsFragment: CommunityPageFriendsComponent.Builder

    fun inject(fragment: CommunityPageFragment)
}