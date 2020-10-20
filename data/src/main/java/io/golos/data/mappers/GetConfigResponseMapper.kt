package io.golos.data.mappers

import io.golos.commun4j.services.model.GetConfigResponse
import io.golos.domain.dto.ConfigDomain

fun GetConfigResponse.mapToConfigDomain() =
    ConfigDomain(
        ftueCommunityBonus = ftueCommunityBonus,
        domain = domain,
        isNeedAppUpdate = false
    )