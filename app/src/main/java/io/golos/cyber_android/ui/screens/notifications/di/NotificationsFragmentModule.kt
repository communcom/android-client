package io.golos.cyber_android.ui.screens.notifications.di

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.cyber_android.ui.shared.recycler_view.versioned.VersionedListItem

@Module
class NotificationsFragmentModule {
    @Provides
    internal fun providePaginatorPost(): Paginator.Store<VersionedListItem> = Paginator.Store(Paginator())
}