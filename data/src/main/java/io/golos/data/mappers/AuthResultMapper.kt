package io.golos.data.mappers

import io.golos.commun4j.services.model.AuthResult
import io.golos.domain.dto.AuthResultDomain
import io.golos.domain.dto.UserIdDomain

fun AuthResult.mapToAuthResultDomain() =
    AuthResultDomain(
        permission = permission,
        user = user,
        userId = UserIdDomain(userId.name),
        username = username
)
