package io.golos.cyber_android.application.dependency_injection.user_posts_feed

import dagger.Module
import dagger.Provides
import io.golos.domain.entities.CyberUser

@Module
class UserPostsFeedModule(private val forUser: CyberUser) {
    @Provides
    internal fun provideForUser() = forUser
}