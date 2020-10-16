package io.golos.data

import androidx.lifecycle.MutableLiveData
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.dto.UserIdDomain

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

internal fun String.toCyberName() = CyberName(this)

internal fun UserIdDomain.toCyberName() = this.userId.toCyberName()