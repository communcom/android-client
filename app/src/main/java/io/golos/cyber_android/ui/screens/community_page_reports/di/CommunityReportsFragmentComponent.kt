package io.golos.cyber_android.ui.screens.community_page_reports.di

import dagger.Subcomponent
import io.golos.cyber_android.ui.screens.community_page_reports.view.CommunityReportsFragment
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.di.CommunityCommentReportsFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.di.CommunityPostReportsFragmentComponent
import io.golos.domain.dependency_injection.scopes.FragmentScope

@Subcomponent(modules = [CommunityReportsModuleBinds::class,  CommunityReportsFragmentModule::class,CommunityReportsFragmentComponentChilds::class])
@FragmentScope
interface CommunityReportsFragmentComponent {

    @Subcomponent.Builder
    interface Builder {
        fun init(module: CommunityReportsFragmentModule): Builder
        fun build(): CommunityReportsFragmentComponent
    }
    val communityPostReportFragment: CommunityPostReportsFragmentComponent.Builder
    val communityCommentReportFragment: CommunityCommentReportsFragmentComponent.Builder
    fun inject(fragment: CommunityReportsFragment)

}