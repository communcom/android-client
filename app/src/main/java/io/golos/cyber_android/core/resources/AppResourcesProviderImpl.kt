package io.golos.cyber_android.core.resources

import android.content.Context
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import io.golos.cyber_android.R
import io.golos.domain.AppResourcesProvider
import java.io.InputStream
import java.text.MessageFormat
import javax.inject.Inject

class AppResourcesProviderImpl
@Inject
constructor(
    private val appContext: Context
) : AppResourcesProvider {

    override fun getRaw(resId: Int): InputStream = appContext.resources.openRawResource(resId)

    override fun getCountries(): InputStream = getRaw(R.raw.countries)

    override fun getCommunities(): InputStream = getRaw(R.raw.communities)

    override fun getString(resId: Int): String = appContext.getString(resId)

    override fun getFormattedString(resId: Int, vararg args: Any): String = MessageFormat.format(getString(resId), *args)

    override fun getFormattedString(string: String, vararg args: Any): String = MessageFormat.format(string, *args)

    override fun getQuantityString(resId: Int, quantity: Int): String = appContext.resources.getQuantityString(resId, quantity)

    override fun getDimens(@DimenRes resId: Int): Float = appContext.resources.getDimension(resId)

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