package io.golos.cyber_android.ui.screens.profile_bio.di

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