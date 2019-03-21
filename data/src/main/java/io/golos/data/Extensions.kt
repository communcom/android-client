package io.golos.data

import androidx.lifecycle.MutableLiveData
import io.golos.cyber4j.model.CyberName
import io.golos.domain.entities.CyberUser

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
internal fun CyberUser.toCyberName() = CyberName(this.userId)
internal fun String.toCyberUser() = CyberUser(this)
internal fun String.toCyberName() = CyberName(this)