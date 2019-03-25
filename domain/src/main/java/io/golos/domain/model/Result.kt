package io.golos.domain.model

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */


sealed class QueryResult<Q>(open val originalQuery: Q) {

    class Success<Q>(originalQuery: Q) : QueryResult<Q>(originalQuery){

        override fun toString(): String {
            return "QueryResult.Success(originalQuery = $originalQuery)"
        }
    }
    class Loading<Q>(originalQuery: Q) : QueryResult<Q>(originalQuery){
        override fun toString(): String {
            return "QueryResult.Loading(originalQuery = $originalQuery)"
        }
    }
    class Error<Q>(val error: Throwable, originalQuery: Q) : QueryResult<Q>(originalQuery) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Error<*>

            if (error != other.error) return false
            if (originalQuery != other.originalQuery) return false

            return true
        }

        override fun hashCode(): Int {
            var result = error.hashCode()
            result = 31 * result + (originalQuery?.hashCode() ?: 0)
            return result
        }

        override fun toString(): String {
            return "QueryResult.Error(error=$error)"
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QueryResult<*>

        if (originalQuery != other.originalQuery) return false

        return true
    }


    override fun hashCode(): Int {
        return originalQuery?.hashCode() ?: 0
    }

}
