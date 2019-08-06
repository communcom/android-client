package io.golos.cyber_android.core.logger

import android.util.Log
import io.golos.domain.CrashlyticsFacade
import io.golos.domain.Logger
import javax.inject.Inject

class LoggerImpl
@Inject
constructor(
    private val crashlytics: CrashlyticsFacade
) : Logger {

    override fun log(tag: String, message: String) {
        Log.d("CLOG_$tag", message)
        crashlytics.log(tag, message)
    }

    override fun log(ex: Throwable) {
        ex.printStackTrace()
        crashlytics.log(ex)
    }
}