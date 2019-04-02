package io.golos.domain.rules

import androidx.annotation.WorkerThread
import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
interface CyberToEntityMapper<F, T : Entity> {
    @WorkerThread
    suspend operator fun invoke(cyberObject: F): T
}

interface EntityToCyberMapper<T : Entity, F> {
    @WorkerThread
    suspend operator fun invoke(entity: T): F
}
