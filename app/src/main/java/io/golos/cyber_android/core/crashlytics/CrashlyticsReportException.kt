package io.golos.cyber_android.core.crashlytics

/**
 * Trigger exception to sent user report
 */
class CrashlyticsReportException(message: String): RuntimeException(message)