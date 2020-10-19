package io.golos.cyber_android.ui.screens.community_page_proposals.fragments.proposalsall.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_page_proposals.fragments.proposalsall.CommunityProposalsAllFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [CommunityAllProposalsModuleBinds::class, CommunityAllProposalsFragmentModule::class])
@SubFragmentScope
interface CommunityAllProposalsFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: CommunityAllProposalsFragmentModule): Builder
        fun build(): CommunityAllProposalsFragmentComponent
    }

    fun inject(fragment: CommunityProposalsAllFragment)
}