package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment

import dagger.Module
import dagger.Provides
import io.golos.commun4j.sharedmodel.CyberName

@Module
class ProfileFragmentModule(private val user: CyberName) {
    @Provides
    internal fun provideUser():CyberName = user
}