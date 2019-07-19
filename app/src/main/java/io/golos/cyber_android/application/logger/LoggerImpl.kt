package io.golos.cyber_android.application.logger

import io.golos.domain.Logger
import io.golos.domain.dependency_injection.scopes.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class LoggerImpl
@Inject
constructor(): Logger {
    override fun invoke(e: Exception) {
        e.printStackTrace()
    }
}