package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.model

import io.golos.domain.fingerprint.FingerprintAuthEventHandler
import io.golos.cyber_android.ui.shared.mvvm.model.ModelBase

interface FingerprintAuthModel: ModelBase {
    /**
     * Start an authentication session
     */
    fun startAuth(eventsCallback: FingerprintAuthEventHandler)

    fun cancelAuth()
}