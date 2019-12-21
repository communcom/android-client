package io.golos.domain.dto.bc_profile

data class BCProfilePermissionDomain(
    val permName: String,
    val parent: String,
    val requiredAuth: BCProfileRequiredAuthDomain
)