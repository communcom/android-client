package io.golos.cyber_android.ui.screens.community_page_post.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_page_post.view.CommunityPostFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [CommunityPostModuleBinds::class, CommunityPostFragmentModule::class])
@FragmentScope
interface CommunityPostFragmentComponent {

    @Subcomponent.Builder
    interface Builder {
        fun init(module: CommunityPostFragmentModule): Builder
        fun build(): CommunityPostFragmentComponent
    }

    fun inject(fragment: CommunityPostFragment)

}