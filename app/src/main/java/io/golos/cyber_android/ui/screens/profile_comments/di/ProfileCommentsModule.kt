package io.golos.cyber_android.ui.screens.profile_comments.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dto.UserIdDomain

@Module
class ProfileCommentsModule(private val profileUserId: UserIdDomain) {
    @Provides
    internal fun provideProfileUserId(): UserIdDomain = profileUserId
}