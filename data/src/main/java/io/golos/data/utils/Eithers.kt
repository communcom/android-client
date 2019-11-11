package io.golos.data.utils

import io.golos.commun4j.sharedmodel.Either
import io.golos.data.errors.CyberServicesError

fun <S : Any, F : Any> Either<S, F>.getOrThrow(): S =
    (this as? Either.Success)?.value ?: throw CyberServicesError(this as Either.Failure)