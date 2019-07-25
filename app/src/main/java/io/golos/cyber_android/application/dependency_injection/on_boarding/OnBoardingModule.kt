package io.golos.cyber_android.application.dependency_injection.on_boarding

import dagger.Module
import dagger.Provides
import io.golos.sharedmodel.CyberName

@Module
class OnBoardingModule(private val forUser: CyberName) {
    @Provides
    internal fun provideForUser():CyberName = forUser
}