package io.golos.cyber_android.application.dependency_injection.graph.app.ui.community_page.leads_list_fragment

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
class CommunityPageLeadsListModule(private val communityId: String) {
    @Provides
    @Named(Clarification.COMMUNITY_ID)
    internal fun provideCommunityId(): String = communityId
}