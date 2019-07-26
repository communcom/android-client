package io.golos.domain

interface DeviceInfoService {
    /**
     * Returns ISO 3166-1 code of country of null if the code can't be detected
     */
    fun getCountryCode(): String?
}