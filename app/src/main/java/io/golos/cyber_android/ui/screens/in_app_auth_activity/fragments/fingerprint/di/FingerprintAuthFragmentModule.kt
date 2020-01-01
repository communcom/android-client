package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.di

import androidx.annotation.StringRes
import dagger.Module
import dagger.Provides

@Module
class FingerprintAuthFragmentModule(@StringRes private val headerText: Int) {
    @Provides
    internal fun provideHeaderText() = headerText
}