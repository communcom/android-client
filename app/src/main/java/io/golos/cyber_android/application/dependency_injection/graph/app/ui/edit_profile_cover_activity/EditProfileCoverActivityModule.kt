package io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_cover_activity

import dagger.Module
import dagger.Provides
import io.golos.commun4j.sharedmodel.CyberName

@Module
class EditProfileCoverActivityModule(private val forUser: CyberName) {
    @Provides
    internal fun provideForUser(): CyberName = forUser
}