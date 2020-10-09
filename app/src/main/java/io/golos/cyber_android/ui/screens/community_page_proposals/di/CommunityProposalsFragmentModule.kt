package io.golos.cyber_android.ui.screens.community_page_proposals.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dto.CommunityIdDomain

@Module
class CommunityProposalsFragmentModule(private val communityId: CommunityIdDomain) {

    @Provides
    internal fun provideCommunityId(): CommunityIdDomain = communityId

}