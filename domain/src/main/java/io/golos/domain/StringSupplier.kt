package io.golos.domain

import androidx.annotation.NonNull
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import java.io.InputStream

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-25.
 */
interface StringSupplier {
    @NonNull
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}

interface RawResoursesSupplier {
    fun getResource(@RawRes resId: Int): InputStream
}