package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.feed_fragment

import dagger.Module
import dagger.Provides
import io.golos.cyber4j.sharedmodel.CyberName
import io.golos.domain.entities.CyberUser

@Module
class MyFeedFragmentModule(private val forUser: CyberUser, private val appUser: CyberName) {
    @Provides
    internal fun provideForUser(): CyberUser = forUser

    @Provides
    internal fun provideAppUser(): CyberName = appUser
}