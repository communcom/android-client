package io.golos.domain.fingerprint

interface FingerprintAuthEvent

typealias FingerprintAuthEventHandler = (FingerprintAuthEvent) -> Unit

/**
 * Some fatal error has happened
 */
class FingerprintAuthErrorEvent(
    val message: String?
) : FingerprintAuthEvent

/**
 * A fingerprint was not matched with any stored image
 */
class FingerprintAuthFailEvent : FingerprintAuthEvent

/**
 * A fingerprint was successfully matched
 */
class FingerprintAuthSuccessEvent : FingerprintAuthEvent

/**
 * Some non-fatal error has happened
 */
class FingerprintAuthWarningEvent(
    val message: String?
) : FingerprintAuthEvent