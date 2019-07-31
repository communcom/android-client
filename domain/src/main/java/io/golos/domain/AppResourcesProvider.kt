package io.golos.domain

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import java.io.InputStream

interface AppResourcesProvider {
    fun getRaw(@RawRes resId: Int): InputStream

    fun getCountries(): InputStream

    fun getString(@StringRes resId: Int): String

    @ColorInt
    fun getColor(@ColorRes resId: Int): Int
}