package io.golos.cyber_android.application.shared.fingerprints

import android.os.Build
import androidx.annotation.RequiresApi
import io.golos.cyber_android.application.shared.fingerprints.eventsHandler.FingerprintAuthEventsHandler

interface FingerprintAuthManager {

    val isAuthenticationPossible: Boolean

    /**
     * Returns wrapper to process an authentication
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun getEventsHandler(): FingerprintAuthEventsHandler
}