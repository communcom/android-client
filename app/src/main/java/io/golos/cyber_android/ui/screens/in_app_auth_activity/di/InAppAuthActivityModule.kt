package io.golos.cyber_android.ui.screens.in_app_auth_activity.di

import android.os.Build
import dagger.Module
import dagger.Provides
import io.golos.cyber_android.core.fingerprints.FingerprintAuthManager
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.model.FingerprintAuthModel
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.model.FingerprintAuthModelFakeImpl
import io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.model.FingerprintAuthModelImpl
import io.golos.domain.dependency_injection.scopes.ActivityScope

@Module
class InAppAuthActivityModule {
    @ActivityScope
    @Provides
    fun provideFingerprintAuthModel(fingerprintAuthManager: FingerprintAuthManager): FingerprintAuthModel =
        if(fingerprintAuthManager.isAuthenticationPossible && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintAuthModelImpl(fingerprintAuthManager)
        } else {
            FingerprintAuthModelFakeImpl()
        }
}