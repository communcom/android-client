package io.golos.domain.rules

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-21.
 */
interface RequestApprover<T> {
    fun approve(param: T): Boolean
}