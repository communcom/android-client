package io.golos.cyber_android.core.device_info

import android.content.Context
import android.telephony.TelephonyManager
import io.golos.domain.DeviceInfoService
import java.util.*
import javax.inject.Inject


class DeviceInfoServiceImpl
@Inject
constructor(private val appContext: Context): DeviceInfoService {
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
            ex.printStackTrace()
        }

        return null
    }
}