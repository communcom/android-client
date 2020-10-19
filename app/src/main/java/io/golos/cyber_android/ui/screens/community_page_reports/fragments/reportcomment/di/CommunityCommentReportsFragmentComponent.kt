package io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.view.CommunityCommentReportsFragment
import io.golos.domain.dependency_injection.scopes.SubFragmentScope

@Subcomponent(modules = [CommunityCommentReportsModuleBinds::class, CommunityCommentReportsFragmentModule::class])
@SubFragmentScope
interface CommunityCommentReportsFragmentComponent {
    @Subcomponent.Builder
    interface Builder {
        fun init(module: CommunityCommentReportsFragmentModule): Builder
        fun build(): CommunityCommentReportsFragmentComponent
    }

    fun inject(fragment: CommunityCommentReportsFragment)
}