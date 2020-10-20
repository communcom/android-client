package io.golos.domain.dto

import io.golos.commun4j.model.CyberDiscussionReports

class ReportedPostDomain (
    val post:PostDomain,
    var entityReport: List<EntityReportDomain>?,
    val reports: CyberDiscussionReports?
)