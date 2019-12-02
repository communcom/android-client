package io.golos.cyber_android.ui.screens.post_report

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import timber.log.Timber
import javax.inject.Inject

class PostReportHolder @Inject constructor() {

    private val reportChannel: ConflatedBroadcastChannel<Report>

    init {
        reportChannel = ConflatedBroadcastChannel(Report(""))
    }

    val reportFlow: Flow<Report> = reportChannel.asFlow()

    suspend fun sendReports(report: List<String>) {
        Timber.d("report: ${report.toTypedArray().contentToString()}")
        reportChannel.send(Report(report.toTypedArray().contentToString()))
    }

    data class Report(val reports: String)

    enum class Type {
        SPAM,
        HARASSMENT,
        NIGUTY,
        VIOLENCE,
        FALSENEWS,
        TERRORISM,
        HATESPEECH,
        UNAUTHORIZEDSALES
    }

}