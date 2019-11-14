package io.golos.data.mappers

import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.data.dto.ApiResponseErrorDomain

object ApiResponseErrorToApiResponseErrorDomainMapper {
    fun invoke(source: ApiResponseError): ApiResponseErrorDomain =
        ApiResponseErrorDomain(
            source.id,
            source.error.code,
            source.error.message)
}