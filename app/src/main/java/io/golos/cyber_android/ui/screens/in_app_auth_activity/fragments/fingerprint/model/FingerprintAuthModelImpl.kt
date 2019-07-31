package io.golos.cyber_android.ui.screens.in_app_auth_activity.fragments.fingerprint.model

import android.os.Build
import androidx.annotation.RequiresApi
import io.golos.cyber_android.core.fingerprints.FingerprintAuthManager
import io.golos.cyber_android.core.fingerprints.eventsHandler.FingerprintAuthEventHandler
import io.golos.cyber_android.core.fingerprints.eventsHandler.FingerprintAuthEventsHandler
import io.golos.cyber_android.ui.common.mvvm.model.ModelBaseImpl
import io.golos.domain.dependency_injection.scopes.ActivityScope
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.M)
@ActivityScope
class FingerprintAuthModelImpl
@Inject
constructor(
    private val fingerprintAuthManager: FingerprintAuthManager
) : ModelBaseImpl(),
    FingerprintAuthModel {

    private lateinit var fingerprintAuthEventsHandler: FingerprintAuthEventsHandler

    private val eventsCallbacks = mutableSetOf<FingerprintAuthEventHandler>()

    override fun startAuth(eventsCallback: FingerprintAuthEventHandler) {
        eventsCallbacks.add(eventsCallback)

        fingerprintAuthEventsHandler = fingerprintAuthManager.getEventsHandler()

        fingerprintAuthEventsHandler.start { event ->
            eventsCallbacks.forEach { it(event) }
        }
    }

    override fun cancelAuth() {
        fingerprintAuthEventsHandler.cancel()
    }
}