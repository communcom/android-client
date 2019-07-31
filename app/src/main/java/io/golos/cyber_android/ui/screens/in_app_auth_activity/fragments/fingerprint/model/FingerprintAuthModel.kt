package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.model

import io.golos.cyber_android.core.fingerprints.eventsHandler.FingerprintAuthEventHandler
import io.golos.cyber_android.ui.common.mvvm.model.ModelBase

interface FingerprintAuthModel: ModelBase {
    /**
     * Start an authentication session
     */
    fun startAuth(eventsCallback: FingerprintAuthEventHandler)

    fun cancelAuth()
}