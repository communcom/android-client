package io.golos.domain.extensions

import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.commun4j.sharedmodel.Either

/**
 * Calls [actionSuccess] or [actionFail]
 */
fun <S, F> Either<S, F>.fold(actionSuccess: (S) -> Unit, actionFail: ((F) -> Unit)? = null) {
    if(this is Either.Success) {
        actionSuccess(this.value)
        return
    }

    if(this is Either.Failure) {
        actionFail?.invoke(this.value)
    }
}

/**
 * Transforms success value or returns fail
 */
fun <S, F, NS> Either<S, F>.mapSuccess(mapSuccess: (S) -> NS): Either<NS, F> =
    when(this) {
        is Either.Success -> Either.Success<NS, F>(mapSuccess(this.value))
        is Either.Failure -> Either.Failure<NS, F>(this.value)
    }

/**
 * Transforms success or fail value to other value
 */
fun <S, F, NS> Either<S, F>.mapSuccessOrFail(mapSuccess: (S) -> NS, mapFail: () -> NS): Either<NS, NS> =
    when(this) {
        is Either.Success -> Either.Success<NS, NS>(mapSuccess(this.value))
        is Either.Failure -> Either.Failure<NS, NS>(mapFail())
    }

fun <S, F : Throwable> Either<S, F>.getOrThrow(): S = (this as? Either.Success)?.value ?: throw (this as Either.Failure).value
