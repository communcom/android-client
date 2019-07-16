package io.golos.cyber_android.core.fingerprints

import android.os.Build
import androidx.annotation.RequiresApi
import io.golos.cyber_android.core.fingerprints.eventsHandler.FingerprintAuthEventsHandler

interface FingerprintAuthManager {

    val isAuthenticationPossible: Boolean

    /**
     * Returns wrapper to process an authentication
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun getEventsHandler(): FingerprintAuthEventsHandler
}