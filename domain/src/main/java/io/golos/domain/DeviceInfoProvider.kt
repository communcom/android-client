package io.golos.domain

interface DeviceInfoProvider {
    /**
     * Returns ISO 3166-1 code of country of null if the code can't be detected
     */
    fun getCountryCode(): String?
}