package io.golos.cyber_android.application.shared.logger

import android.util.Log
import io.golos.domain.CrashlyticsFacade
import timber.log.Timber
import javax.inject.Inject

class CrashlyticsTimberTreeRelease
@Inject
constructor(
    private val crashlytics: CrashlyticsFacade
) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if(t != null) {
            t.printStackTrace()
            crashlytics.log(t)
            return
        }

        Log.println(priority, tag ?: "?", message)
    }
}