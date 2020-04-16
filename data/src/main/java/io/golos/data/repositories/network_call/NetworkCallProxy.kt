package io.golos.data.repositories.network_call

import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.sharedmodel.GolosEosError

interface NetworkCallProxy {
    suspend fun <TR>call(action: suspend () -> Either<TR, ApiResponseError>): TR

    suspend fun <TR>callBC(action: suspend () -> Either<TR, GolosEosError>): TR
}