package io.golos.cyber_android.ui.screens.ftue_search_community.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.ftue_search_community.view.FtueSearchCommunityFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [FtueSearchCommunityFragmentModuleBinds::class])
@FragmentScope
interface FtueSearchCommunityFragmentComponent {
    @Subcomponent.Builder
    interface Builder {

        fun build(): FtueSearchCommunityFragmentComponent
    }

    fun inject(fragment: FtueSearchCommunityFragment)
}