package io.golos.cyber_android.ui.screens.profile_posts.di

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.feed_my.view_model.PostPaginator
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem
import io.golos.domain.dto.PostsConfigurationDomain

@Module
class ProfilePostsFragmentModule(private val startFeedType: PostsConfigurationDomain.TypeFeedDomain) {
    @Provides
    internal fun provideStartFeedType(): PostsConfigurationDomain.TypeFeedDomain = startFeedType

    @Provides
    internal fun providePaginatorSubscriptions(): Paginator.Store<VersionedListItem> = Paginator.Store(Paginator())

    @Provides
    internal fun providePaginatorPost(): Paginator.Store<Post> = Paginator.Store(PostPaginator())
}
