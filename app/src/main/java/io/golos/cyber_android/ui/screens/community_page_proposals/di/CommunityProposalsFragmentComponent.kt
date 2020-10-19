package io.golos.cyber_android.ui.screens.community_page_proposals.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_page_proposals.CommunityProposalsFragment
import io.golos.cyber_android.ui.screens.community_page_proposals.fragments.proposalsall.di.CommunityAllProposalsFragmentComponent
import io.golos.cyber_android.ui.screens.discovery.view.fragments.discovery_users.di.DiscoveryUsersFragmentComponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [CommunityProposalsModuleBinds::class, CommunityProposalsFragmentModule::class,CommunityProposalsFragmentComponentChilds::class])
@FragmentScope
interface CommunityProposalsFragmentComponent {

    @Subcomponent.Builder
    interface Builder {
        fun init(module: CommunityProposalsFragmentModule): Builder
        fun build(): CommunityProposalsFragmentComponent
    }
    val  communityAllComponent: CommunityAllProposalsFragmentComponent.Builder

    fun inject(fragment: CommunityProposalsFragment)

}