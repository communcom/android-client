package io.golos.cyber_android.application.shared.crashlytics

/**
 * Trigger exception to sent user report
 */
class CrashlyticsReportException(message: String): RuntimeException(message)