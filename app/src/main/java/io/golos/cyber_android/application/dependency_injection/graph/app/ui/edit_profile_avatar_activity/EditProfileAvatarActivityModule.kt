package io.golos.cyber_android.application.dependency_injection.graph.app.ui.edit_profile_avatar_activity

import dagger.Module
import dagger.Provides
import io.golos.sharedmodel.CyberName

@Module
class EditProfileAvatarActivityModule(private val forUser: CyberName) {
    @Provides
    internal fun provideForUser(): CyberName = forUser
}