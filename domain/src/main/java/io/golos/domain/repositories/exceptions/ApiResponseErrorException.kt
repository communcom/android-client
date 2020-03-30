package io.golos.domain.repositories.exceptions

class ApiResponseErrorException(
    val errorInfo: ApiResponseErrorDomain
) : Exception(errorInfo.message)