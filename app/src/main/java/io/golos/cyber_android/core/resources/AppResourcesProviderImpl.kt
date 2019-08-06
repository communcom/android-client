package io.golos.cyber_android.core.resources

import android.content.Context
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import io.golos.cyber_android.R
import io.golos.domain.AppResourcesProvider
import java.io.InputStream
import javax.inject.Inject

class AppResourcesProviderImpl
@Inject
constructor(
    private val appContext: Context
) : AppResourcesProvider {

    override fun getRaw(resId: Int): InputStream = appContext.resources.openRawResource(resId)

    override fun getCountries(): InputStream = getRaw(R.raw.countries)

    override fun getString(resId: Int): String = appContext.getString(resId)

    @Suppress("DEPRECATION")
    @ColorInt
    override fun getColor(@ColorRes resId: Int): Int =
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            appContext.resources.getColor(resId, null)
        } else {
            appContext.resources.getColor(resId)
        }

    override fun getLocale(): String = getString(R.string.locale)
}