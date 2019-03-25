package io.golos.domain

import androidx.annotation.NonNull
import androidx.annotation.StringRes

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-25.
 */
interface StringSupplier {
    @NonNull
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}