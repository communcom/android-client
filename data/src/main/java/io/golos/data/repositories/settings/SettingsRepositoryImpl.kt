package io.golos.data.repositories.settings

import io.golos.commun4j.Commun4j
import io.golos.data.mappers.mapToConfigDomain
import io.golos.data.repositories.network_call.NetworkCallProxy
import io.golos.domain.dto.ConfigDomain
import io.golos.domain.repositories.exceptions.ApiResponseErrorException
import io.golos.utils.BuildConfig
import javax.inject.Inject

class SettingsRepositoryImpl
@Inject
constructor(
    private val callProxy: NetworkCallProxy,
    private val commun4j: Commun4j
) : SettingsRepository {

    override suspend fun getConfig(): ConfigDomain {
        val versionName = BuildConfig.VERSION_NAME
        return try {
            callProxy.call { commun4j.getConfig() }.mapToConfigDomain()
        } catch (ex: ApiResponseErrorException) {
            if(ex.errorInfo.code == 5000L) {
                ConfigDomain(0, "", true)
            } else {
                throw ex
            }
        }
    }
}