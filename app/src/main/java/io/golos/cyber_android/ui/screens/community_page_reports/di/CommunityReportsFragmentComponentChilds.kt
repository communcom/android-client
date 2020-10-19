package io.golos.cyber_android.ui.screens.community_page_reports.di

import dagger.Module
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportcomment.di.CommunityCommentReportsFragmentComponent
import io.golos.cyber_android.ui.screens.community_page_reports.fragments.reportpost.di.CommunityPostReportsFragmentComponent

@Module(subcomponents = [
    CommunityPostReportsFragmentComponent::class,
    CommunityCommentReportsFragmentComponent::class

])
class CommunityReportsFragmentComponentChilds