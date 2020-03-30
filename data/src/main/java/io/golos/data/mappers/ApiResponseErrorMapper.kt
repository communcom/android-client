package io.golos.data.mappers

import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.commun4j.sharedmodel.GolosEosError
import io.golos.domain.repositories.exceptions.ApiResponseErrorDomain
import io.golos.utils.helpers.capitalize

fun ApiResponseError.mapToApiResponseErrorDomain(): ApiResponseErrorDomain =
    ApiResponseErrorDomain(
        id,
        error.code,
        error.message
    )

fun GolosEosError.mapToApiResponseErrorDomain(): ApiResponseErrorDomain {
    val messageToken = "assertion failure with message: "

    val extractedMessage = if(error != null && error!!.details.any { it.message!=null }) {
        val firstMessage = error!!.details.firstOrNull { it.message != null && it.message!!.startsWith(messageToken) }!!.message
        firstMessage?.replace(messageToken, "")?.capitalize() ?: message
    } else {
        message
    }

    return ApiResponseErrorDomain(
        id = code.toLong(),
        code = error?.code?.toLong(),
        message = extractedMessage
    )
}
