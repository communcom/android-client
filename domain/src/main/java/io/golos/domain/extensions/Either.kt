package io.golos.domain.extensions

import io.golos.sharedmodel.Either

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
fun <S, F, NS> Either<S, F>.foldValue(mapSuccess: (S) -> NS): Either<NS, F> =
    when(this) {
        is Either.Success -> Either.Success<NS, F>(mapSuccess(this.value))
        is Either.Failure -> Either.Failure<NS, F>(this.value)
    }
