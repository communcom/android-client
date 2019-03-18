package io.golos.domain

import androidx.annotation.AnyThread

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-12.
 */
interface Logger {
    @AnyThread
    operator fun invoke(e: Exception)
}