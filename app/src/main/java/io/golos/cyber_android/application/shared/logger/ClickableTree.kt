package io.golos.cyber_android.application.shared.logger

import timber.log.Timber

class ClickableTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        with(element) {
            return "($fileName:$lineNumber)$methodName()"
        }
    }
}