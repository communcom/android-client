package io.golos.domain.repositories.exceptions

data class ApiResponseErrorDomain(
    val id: Long,

    val code: Long?,
    val message: String?
)