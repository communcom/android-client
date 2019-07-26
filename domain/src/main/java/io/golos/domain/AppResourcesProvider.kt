package io.golos.domain

import androidx.annotation.RawRes
import java.io.InputStream

interface AppResourcesProvider {
    fun getRaw(@RawRes resId: Int): InputStream

    fun getCountries(): InputStream
}