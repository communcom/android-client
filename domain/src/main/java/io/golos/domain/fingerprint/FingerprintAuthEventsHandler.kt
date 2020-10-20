package io.golos.domain.fingerprint

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