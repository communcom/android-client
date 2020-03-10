package io.golos.data.repositories.settings

import android.content.Context
import io.golos.commun4j.Commun4j
import io.golos.data.exceptions.ApiResponseErrorException
import io.golos.data.mappers.mapToConfigDomain
import io.golos.data.network_state.NetworkStateChecker
import io.golos.data.repositories.RepositoryBase
import io.golos.domain.DispatchersProvider
import io.golos.domain.dto.ConfigDomain
import io.golos.utils.BuildConfig
import javax.inject.Inject

class SettingsRepositoryImpl
@Inject
constructor(
    private val appContext: Context,
    dispatchersProvider: DispatchersProvider,
    networkStateChecker: NetworkStateChecker,
    private val commun4j: Commun4j
) : RepositoryBase(dispatchersProvider, networkStateChecker),
    SettingsRepository {

    override suspend fun getConfig(): ConfigDomain {
        val versionName = BuildConfig.VERSION_NAME
        return try {
            apiCall { commun4j.getConfig() }.mapToConfigDomain()
        } catch (ex: ApiResponseErrorException) {
            if(ex.errorInfo.code == 5000L) {
                ConfigDomain(0, "", true)
            } else {
                throw ex
            }
        }
    }
}