package io.golos.data.mappers

import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.domain.dto.ApiResponseErrorDomain

fun ApiResponseError.mapToApiResponseErrorDomain(): ApiResponseErrorDomain =
    ApiResponseErrorDomain(
        id,
        error.code,
        error.message
    )
