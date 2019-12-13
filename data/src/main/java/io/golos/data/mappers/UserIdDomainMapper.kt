package io.golos.data.mappers

import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.dto.UserIdDomain

fun UserIdDomain.mapToCyberName(): CyberName{
    return CyberName(this.userId)
}