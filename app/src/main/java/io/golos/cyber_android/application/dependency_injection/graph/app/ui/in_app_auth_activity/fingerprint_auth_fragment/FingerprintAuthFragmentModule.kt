package io.golos.cyber_android.application.dependency_injection.graph.app.ui.in_app_auth_activity.fingerprint_auth_fragment

import androidx.annotation.StringRes
import dagger.Module
import dagger.Provides

@Module
class FingerprintAuthFragmentModule(@StringRes private val headerText: Int) {
    @Provides
    internal fun provideHeaderText() = headerText
}