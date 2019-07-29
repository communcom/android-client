package io.golos.cyber_android.application.dependency_injection.graph.app.ui.main_activity.profile_fragment

import dagger.Module
import dagger.Provides
import io.golos.sharedmodel.CyberName

@Module
class ProfileFragmentModule(private val forUser: CyberName) {
    @Provides
    internal fun provideForUser():CyberName = forUser
}