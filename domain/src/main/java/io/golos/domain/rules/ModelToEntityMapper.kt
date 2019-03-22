package io.golos.domain.rules

import androidx.annotation.WorkerThread
import io.golos.domain.Entity
import io.golos.domain.Model

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-21.
 */
interface ModelToEntityMapper<M : Model, E : Entity> {
    @WorkerThread
    suspend operator fun invoke(model: M): E
}