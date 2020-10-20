package io.golos.cyber_android.ui.screens.community_page_reports.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dto.CommunityIdDomain

@Module
class CommunityReportsFragmentModule(private val communityId: CommunityIdDomain) {

    @Provides
    internal fun provideCommunityId(): CommunityIdDomain = communityId

}