package io.golos.cyber_android.ui.screens.profile.di

import dagger.Module
import dagger.Provides
import io.golos.domain.dto.UserIdDomain

@Module
class ProfileFragmentModule(private val profileUserId: UserIdDomain) {
    @Provides
    internal fun provideProfileUserId(): UserIdDomain = profileUserId
}