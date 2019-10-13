package io.golos.cyber_android.core.logger

import timber.log.Timber
import okhttp3.logging.HttpLoggingInterceptor.Logger as HttpLogger

class Cyber4JLogger(private val tag: String): HttpLogger {
    companion object {
        const val HTTP = "NET_HTTP"
        const val SOCKET = "NET_SOCKET"
    }
    override fun log(message: String) = Timber.tag(tag).d(message)
}