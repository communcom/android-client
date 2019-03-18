package io.golos.domain.rules

import androidx.annotation.WorkerThread
import io.golos.domain.Entity
import io.golos.domain.Model

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
interface ModelToEntityMapper<F : Model, T : Entity> {
    @WorkerThread
    suspend operator fun invoke(model: F): T
}