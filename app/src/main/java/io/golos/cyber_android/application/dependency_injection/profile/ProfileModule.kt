package io.golos.cyber_android.application.dependency_injection.profile

import dagger.Module
import dagger.Provides
import io.golos.sharedmodel.CyberName

@Module
class ProfileModule(private val forUser: CyberName) {
    @Provides
    internal fun provideForUser():CyberName = forUser
}