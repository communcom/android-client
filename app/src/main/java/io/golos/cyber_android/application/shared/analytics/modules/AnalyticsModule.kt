package io.golos.cyber_android.application.shared.analytics.modules

import android.app.Application
import io.golos.domain.analytics.AnalyticsEvents

interface AnalyticsModule : AnalyticsEvents {
    fun init(app: Application)
}