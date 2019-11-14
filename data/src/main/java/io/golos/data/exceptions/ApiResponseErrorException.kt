package io.golos.data.exceptions

import io.golos.data.dto.ApiResponseErrorDomain

class ApiResponseErrorException(
    val errorInfo: ApiResponseErrorDomain
) : Exception(errorInfo.message)