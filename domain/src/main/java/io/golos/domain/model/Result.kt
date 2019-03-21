package io.golos.domain.model

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Loading<T>(val data: T) : Result<T>()
    data class Error<T>(val error: Throwable, val data: T?) : Result<T>()
}

sealed class QueryResult<Q>(open val originalQuery: Q) {
    data class Success<Q>(override val originalQuery: Q) : QueryResult<Q>(originalQuery)
    data class Loading<Q>(override val originalQuery: Q) : QueryResult<Q>(originalQuery)
    data class Error<Q>(val error: Throwable, override val originalQuery: Q) : QueryResult<Q>(originalQuery)
}
