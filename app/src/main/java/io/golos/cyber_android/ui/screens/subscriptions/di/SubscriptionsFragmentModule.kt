package io.golos.cyber_android.ui.screens.subscriptions.di

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.screens.subscriptions.Community
import io.golos.cyber_android.ui.shared.paginator.Paginator
import javax.inject.Named

@Module
class SubscriptionsFragmentModule {
    @Provides
    @Named("PaginatorRecomendedCommunities")
    fun providesPaginatorRecomendedCommunities(): Paginator.Store<Community> = Paginator.Store(Paginator())

    @Provides
    @Named("PaginatorSubscriptions")
    fun providesPaginatorSubscriptions(): Paginator.Store<Community> = Paginator.Store(Paginator())
}