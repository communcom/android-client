package io.golos.cyber_android.ui.screens.login_activity.di.on_boarding

import dagger.Module
import dagger.Provides
import io.golos.commun4j.sharedmodel.CyberName

@Module
class OnBoardingFragmentModule(private val forUser: CyberName) {
    @Provides
    internal fun provideForUser():CyberName = forUser
}