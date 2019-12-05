package io.golos.cyber_android.ui.screens.ftuesearchcommunity.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.ftuesearchcommunity.view.FtueSearchCommunityFragment
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [FtueSearchCommunityModuleBinds::class])
@FragmentScope
interface FtueSearchCommunityComponent {
    @Subcomponent.Builder
    interface Builder {

        fun build(): FtueSearchCommunityComponent
    }

    fun inject(fragment: FtueSearchCommunityFragment)
}