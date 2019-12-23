package io.golos.domain.dto.bc_profile

data class BCProfileRequiredAuthDomain(
    val threshold: Int,
    val keys: List<BCProfileKeyDomain>,
    val accounts: List<BCProfileAuthDomain>
)