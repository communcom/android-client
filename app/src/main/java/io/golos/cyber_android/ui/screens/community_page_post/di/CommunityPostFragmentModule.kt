package io.golos.cyber_android.ui.screens.community_page_post.di

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dependency_injection.Clarification
import io.golos.domain.dto.CommunityIdDomain
import javax.inject.Named

@Module
class CommunityPostFragmentModule(private val communityId: CommunityIdDomain) {
    @Provides
    internal fun provideCommunityId(): CommunityIdDomain = communityId

    @Provides
    internal fun providePaginatorSubscriptions(): Paginator.Store<VersionedListItem> = Paginator.Store(Paginator())

    @Provides
    internal fun providePaginatorPost(): Paginator.Store<Post> = Paginator.Store(Paginator())
}