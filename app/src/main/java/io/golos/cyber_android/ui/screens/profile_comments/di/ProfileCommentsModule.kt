package io.golos.cyber_android.ui.screens.profile_comments.di

import dagger.Module
import dagger.Provides
import io.golos.cyber_android.ui.dto.Post
import io.golos.cyber_android.ui.shared.paginator.Paginator
import io.golos.domain.dto.CommentDomain
import io.golos.domain.dto.UserIdDomain

@Module
class ProfileCommentsModule(private val profileUserId: UserIdDomain) {
    @Provides
    internal fun provideProfileUserId(): UserIdDomain = profileUserId

    @Provides
    internal fun providePaginatorPost(): Paginator.Store<CommentDomain> = Paginator.Store(Paginator())
}