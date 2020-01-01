package io.golos.cyber_android.ui.screens.login_sign_up_bio.di

import dagger.Module
import dagger.Provides
import io.golos.commun4j.sharedmodel.CyberName

@Module
class BioFragmentModule(private val forUser: CyberName) {
    @Provides
    internal fun provideForUser():CyberName = forUser
}