package io.golos.cyber_android.ui.screens.community_page_members.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_page_members.view.CommunityPageMembersFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [CommunityPageMembersModuleBinds::class, CommunityPageMembersModule::class])
@SubFragmentScope
interface CommunityPageMembersComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: CommunityPageMembersModule): Builder
        fun build(): CommunityPageMembersComponent
    }

    fun inject(fragment: CommunityPageMembersFragment)
}