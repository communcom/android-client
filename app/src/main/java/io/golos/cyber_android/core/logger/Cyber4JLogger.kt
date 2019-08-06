package io.golos.cyber_android.core.logger

import io.golos.domain.Logger
import okhttp3.logging.HttpLoggingInterceptor.Logger as HttpLogger

class Cyber4JLogger(private val logger: Logger, private val tag: String): HttpLogger {
    companion object {
        const val HTTP = "NET_HTTP"
        const val SOCKET = "NET_SOCKET"
    }
    override fun log(message: String) = logger.log(tag, message)
}