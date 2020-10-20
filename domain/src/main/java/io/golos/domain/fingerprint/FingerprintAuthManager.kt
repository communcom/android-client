package io.golos.domain.fingerprint

import android.os.Build
import androidx.annotation.RequiresApi

interface FingerprintAuthManager {

    val isAuthenticationPossible: Boolean

    /**
     * Returns wrapper to process an authentication
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun getEventsHandler(): FingerprintAuthEventsHandler
}