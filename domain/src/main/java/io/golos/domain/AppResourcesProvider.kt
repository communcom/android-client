package io.golos.domain

import androidx.annotation.*
import java.io.InputStream

interface AppResourcesProvider {
    fun getRaw(@RawRes resId: Int): InputStream

    fun getCountries(): InputStream

    fun getCommunities(): InputStream

    fun getString(@StringRes resId: Int): String

    fun getFormattedString(@StringRes resId: Int, vararg args: Any): String

    fun getFormattedString(string: String, vararg args: Any): String

    fun getQuantityString(@PluralsRes resId: Int, quantity: Int): String

    fun getDimens(@DimenRes resId: Int): Float

    @ColorInt
    fun getColor(@ColorRes resId: Int): Int

    fun getLocale(): String
}