package io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.view.CommunityPostReportsFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [CommunityPostReportsModuleBinds::class, CommunityPostReportsFragmentModule::class])
@SubFragmentScope
interface CommunityPostReportsFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: CommunityPostReportsFragmentModule): Builder
        fun build(): CommunityPostReportsFragmentComponent
    }

    fun inject(fragment: CommunityPostReportsFragment)
}