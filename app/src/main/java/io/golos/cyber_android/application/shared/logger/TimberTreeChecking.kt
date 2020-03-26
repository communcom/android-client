package io.golos.cyber_android.application.shared.logger

import android.util.Log
import timber.log.Timber
import zerobranch.androidremotedebugger.AndroidRemoteDebugger
import javax.inject.Inject

class TimberTreeChecking
@Inject
constructor() : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        t?.printStackTrace()

        AndroidRemoteDebugger.Log.log(priority, tag, message, t)

        Log.println(priority, tag, message)
    }
}