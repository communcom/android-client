package io.golos.cyber_android.ui.screens.post_report

import io.golos.cyber_android.ui.dto.Post
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class PostReportHolder @Inject constructor() {

    private val reportChannel: ConflatedBroadcastChannel<Report> = ConflatedBroadcastChannel()

    val reportFlow: Flow<Report> = reportChannel.asFlow()

    suspend fun sendReports(report: Report) {
        reportChannel.send(report)
    }

    data class Report(val reports: List<String>, val contentId: Post.ContentId)

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