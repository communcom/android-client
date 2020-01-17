package io.golos.cyber_android.ui.screens.community_page_friends.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_page_friends.view.CommunityPageFriendsFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [CommunityPageFriendsModuleBinds::class, CommunityPageFriendsModule::class])
@SubFragmentScope
interface CommunityPageFriendsComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: CommunityPageFriendsModule): Builder
        fun build(): CommunityPageFriendsComponent
    }

    fun inject(fragment: CommunityPageFriendsFragment)
}