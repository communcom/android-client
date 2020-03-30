@file:Suppress("DEPRECATION")

package io.golos.cyber_android.application.shared.fingerprints.eventsHandler

import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.RequiresApi
import io.golos.domain.fingerprint.*

@RequiresApi(Build.VERSION_CODES.M)
class FingerprintAuthEventsHandlerImpl(
    private val manager: FingerprintManager,
    private val cryptoObject: FingerprintManager.CryptoObject
) : FingerprintManager.AuthenticationCallback(),
    FingerprintAuthEventsHandler {

    private lateinit var cancellationSignal: CancellationSignal

    private lateinit var eventsCallback: FingerprintAuthEventHandler

    private var isInTerminalState = false

    /**
     * Start an authentication session
     */
    override fun start(eventsCallback: FingerprintAuthEventHandler) {
        this.eventsCallback = eventsCallback

        cancellationSignal = CancellationSignal()
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    /**
     * Cancel an authentication session
     */
    override fun cancel() {
        cancellationSignal.takeIf { !isInTerminalState && !it.isCanceled }?.cancel()
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        super.onAuthenticationError(errorCode, errString)
        isInTerminalState = true
        eventsCallback(FingerprintAuthErrorEvent(errString?.toString()))
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        eventsCallback(FingerprintAuthFailEvent())
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
        super.onAuthenticationHelp(helpCode, helpString)
        eventsCallback(FingerprintAuthWarningEvent(helpString?.toString()))
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        super.onAuthenticationSucceeded(result)
        isInTerminalState = true
        eventsCallback(FingerprintAuthSuccessEvent())
    }
}