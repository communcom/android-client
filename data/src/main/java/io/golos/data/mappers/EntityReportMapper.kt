package io.golos.data.mappers

import io.golos.commun4j.services.model.EntityReportModel
import io.golos.domain.dto.EntityReportDomain

fun EntityReportModel.mapTReportDomain(): EntityReportDomain {
    return EntityReportDomain(reason = reason ?: "", author = author?.mapToAuthorDomain())
}