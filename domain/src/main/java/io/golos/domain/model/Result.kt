package io.golos.domain.model

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Loading<T>(val data: T) : Result<T>()
    data class Error<T>(val error: Throwable, val data: T?) : Result<T>()
}
