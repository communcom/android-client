package io.golos.cyber_android.ui.screens.feed_my.di

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.screens.feed_my.view_model.PostPaginator
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

@Module
class MyFeedFragmentModule {
    @Provides
    internal fun providePaginatorSubscriptions(): Paginator.Store<VersionedListItem> = Paginator.Store(Paginator())

    @Provides
    internal fun providePaginatorPost(): Paginator.Store<Post> = Paginator.Store(PostPaginator())
}