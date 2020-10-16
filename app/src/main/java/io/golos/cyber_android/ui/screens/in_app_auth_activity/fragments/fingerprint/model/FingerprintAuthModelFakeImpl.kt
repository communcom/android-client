package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.model

import io.golos.cyber_android.ui.shared.mvvm.model.ModelBaseImpl
import io.golos.domain.dependency_injection.scopes.ActivityScope
import io.golos.domain.fingerprint.FingerprintAuthEventHandler
import javax.inject.Inject

@ActivityScope
class FingerprintAuthModelFakeImpl
@Inject
constructor(): ModelBaseImpl(),
    FingerprintAuthModel {

    override fun startAuth(eventsCallback: FingerprintAuthEventHandler) {
        // do nothing
    }

    override fun cancelAuth() {
        // do nothing
    }
}