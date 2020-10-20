package io.golos.domain.dto.bc_profile

data class BCProfileAuthDomain(
    val permission: BCProfileAuthPermissionDomain,
    val weight: Int
)