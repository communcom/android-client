package io.golos.domain.commun_entities

import io.golos.commun4j.services.model.GetProfileResult

data class GetProfileResultExt(
    val profile: GetProfileResult,
    val avatarUrl: String?
)