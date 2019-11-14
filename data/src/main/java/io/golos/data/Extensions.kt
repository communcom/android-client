package io.golos.data

import androidx.lifecycle.MutableLiveData
import io.golos.commun4j.http.rpc.model.ApiResponseError
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.Either
import io.golos.data.exceptions.ApiResponseErrorException
import io.golos.data.mappers.ApiResponseErrorToApiResponseErrorDomainMapper
import io.golos.domain.dto.CyberUser

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */

internal fun <K, V> MutableMap<K, MutableLiveData<V>>.putIfAbsentAndGet(params: K): MutableLiveData<V> {
    if (!this.containsKey(params))
        this[params] = MutableLiveData()
    return this[params]!!
}

internal fun <E> List<E>.replace(
    identifier: (E) -> Boolean,
    replaceWith: E
) = this.toMutableList().map { item ->
    if (identifier(item)) replaceWith
    else item

}

internal fun <E> List<E>.replaceByProducer(
    identifier: (E) -> Boolean,
    producer: (E) -> E
) = this.toMutableList().map { item ->
    if (identifier(item)) producer(item)
    else item

}

internal fun CyberName.toCyberUser() = CyberUser(this.name)
internal fun String.toCyberUser() = CyberUser(this)
internal fun String.toCyberName() = CyberName(this)

fun <S> Either<S, ApiResponseError>.getOrThrow(): S =
    (this as? Either.Success)?.value
        ?:
    throw ApiResponseErrorException(ApiResponseErrorToApiResponseErrorDomainMapper.invoke((this as Either.Failure).value))