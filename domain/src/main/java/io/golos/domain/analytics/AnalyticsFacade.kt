package io.golos.domain.analytics

import io.golos.domain.analytics.AnalyticsEvents

interface AnalyticsFacade: AnalyticsEvents {
    fun init()
}