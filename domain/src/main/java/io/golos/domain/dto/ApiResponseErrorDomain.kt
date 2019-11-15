package io.golos.domain.dto

data class ApiResponseErrorDomain(
    val id: Long,

    val code: Long,
    val message: String
)