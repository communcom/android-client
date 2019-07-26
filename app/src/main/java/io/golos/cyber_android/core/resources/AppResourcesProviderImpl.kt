package io.golos.cyber_android.core.resources

import android.content.Context
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
}