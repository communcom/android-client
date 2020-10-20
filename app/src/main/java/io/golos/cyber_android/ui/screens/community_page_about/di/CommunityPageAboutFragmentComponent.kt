package io.golos.cyber_android.ui.screens.community_page_about.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_page_about.CommunityPageAboutFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(
    modules = [CommunityPageAboutFragmentModuleBinds::class,
        CommunityPageAboutFragmentModule::class]
)
@FragmentScope
interface CommunityPageAboutFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: CommunityPageAboutFragmentModule): Builder

        fun build(): CommunityPageAboutFragmentComponent
    }

    fun inject(fragment: CommunityPageAboutFragment)
}