package io.golos.cyber_android.ui.screens.ftue_search_community.di

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.dto.Community
import io.golos.cyber_android.ui.shared.paginator.Paginator

@Module
class FtueSearchCommunityFragmentModule {
    @Provides
    internal fun providePaginatorPost(): Paginator.Store<Community> = Paginator.Store(Paginator())
}