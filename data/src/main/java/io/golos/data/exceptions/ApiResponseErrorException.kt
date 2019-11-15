package io.golos.data.exceptions

import io.golos.domain.dto.ApiResponseErrorDomain

class ApiResponseErrorException(
    val errorInfo: ApiResponseErrorDomain
) : Exception(errorInfo.message)