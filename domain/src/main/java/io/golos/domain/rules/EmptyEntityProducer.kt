package io.golos.domain.rules

import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-12.
 */
interface EmptyEntityProducer<T : Entity> {
    operator fun invoke(): T
}