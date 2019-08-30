package io.golos.cyber_android.application.dependency_injection.graph.app.ui.bio_fragment

import dagger.Module
import dagger.Provides
import io.golos.cyber4j.sharedmodel.CyberName

@Module
class BioFragmentModule(private val forUser: CyberName) {
    @Provides
    internal fun provideForUser():CyberName = forUser
}