package io.golos.cyber_android.application.dependency_injection.my_feed

import dagger.Module
import dagger.Provides
import io.golos.domain.entities.CyberUser
import io.golos.sharedmodel.CyberName

@Module
class MyFeedModule(private val forUser: CyberUser, private val appUser: CyberName) {
    @Provides
    internal fun provideForUser() = forUser

    @Provides
    internal fun provideAppUser() = appUser
}