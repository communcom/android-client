package io.golos.cyber_android.core.device_info

import android.content.Context
import android.telephony.TelephonyManager
import io.golos.cyber_android.application.App
import io.golos.domain.DeviceInfoProvider
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class DeviceInfoProviderImpl
@Inject
constructor(
    private val appContext: Context
) : DeviceInfoProvider {
    override fun getCountryCode(): String? {
        try {
            val tm = appContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            val simCountry = tm.simCountryIso

            // SIM country code is available
            if (simCountry != null && simCountry.length == 2) {
                return simCountry.toLowerCase(Locale.US)
            }

            // device is not 3G (would be unreliable)
            if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) {
                val networkCountry = tm.networkCountryIso
                if (networkCountry != null && networkCountry.length == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US)
                }
            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }

        return null
    }
}