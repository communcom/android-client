package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.feed_fragment

import dagger.Module
import dagger.Provides
import io.golos.domain.entities.CyberUser
import io.golos.sharedmodel.CyberName

@Module
class MyFeedFragmentModule(private val forUser: CyberUser, private val appUser: CyberName) {
    @Provides
    internal fun provideForUser() = forUser

    @Provides
    internal fun provideAppUser() = appUser
}