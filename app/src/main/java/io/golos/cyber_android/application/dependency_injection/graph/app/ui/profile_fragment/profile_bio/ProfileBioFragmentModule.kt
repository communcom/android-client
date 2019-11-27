package io.golos.cyber_android.application.dependency_injection.graph.app.ui.profile_fragment.profile_bio

import dagger.Module
import dagger.Provides
import io.golos.domain.dependency_injection.Clarification
import javax.inject.Named

@Module
class ProfileBioFragmentModule(private val text: String?) {
    @Provides
    @Named(Clarification.TEXT)
    fun provideText(): String? = text
}