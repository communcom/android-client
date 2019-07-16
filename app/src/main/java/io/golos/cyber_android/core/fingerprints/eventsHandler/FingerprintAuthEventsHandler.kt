package io.golos.cyber_android.core.fingerprints.eventsHandler

interface FingerprintAuthEventsHandler {
    /**
     * Start an authentication session
     */
    fun start(eventsCallback: FingerprintAuthEventHandler)

    /**
     * Cancel an authentication session
     */
    fun cancel()
}