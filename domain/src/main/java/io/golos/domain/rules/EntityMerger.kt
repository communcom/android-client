package io.golos.domain.rules

import androidx.annotation.WorkerThread
import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-12.
 */
interface EntityMerger<T : Entity> {
    @WorkerThread
    operator fun invoke(new: T, old: T): T
}