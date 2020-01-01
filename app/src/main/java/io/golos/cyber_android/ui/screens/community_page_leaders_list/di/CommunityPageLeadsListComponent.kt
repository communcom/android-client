package io.golos.cyber_android.ui.screens.community_page_leaders_list.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_page_leaders_list.view.LeadsListFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [
    CommunityPageLeadsListModule::class,
    CommunityPageLeadsListModuleBinds::class
])
@SubFragmentScope
interface CommunityPageLeadsListComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: CommunityPageLeadsListModule): Builder
        fun build(): CommunityPageLeadsListComponent
    }

    fun inject(fragment: LeadsListFragment)
}