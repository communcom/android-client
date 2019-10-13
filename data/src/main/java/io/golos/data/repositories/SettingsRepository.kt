package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.data.api.settings.SettingsApi
import io.golos.domain.*
import io.golos.domain.entities.UserSettingEntity
import io.golos.domain.mappers.SettingToCyberMapper
import io.golos.domain.mappers.SettingsToEntityMapper
import io.golos.domain.repositories.Repository
import io.golos.domain.requestmodel.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-26.
 */
class SettingsRepository
@Inject
constructor(
    private val api: SettingsApi,
    private val toEntityMapper: SettingsToEntityMapper,
    private val dispatchersProvider: DispatchersProvider,
    private val deviceIdProvider: DeviceIdProvider,
    defaultUserSettingsProvider: DefaultSettingProvider
) : Repository<UserSettingEntity, SettingChangeRequest> {

    private val userSettings = MutableLiveData(defaultUserSettingsProvider.provide())
    private val updatingStates = MutableLiveData<Map<Identifiable.Id, QueryResult<SettingChangeRequest>>>()
    private val jobsMap = Collections.synchronizedMap(HashMap<Identifiable.Id, Job>())
    private val repositoryScope = CoroutineScope(dispatchersProvider.uiDispatcher + SupervisorJob())

    override val allDataRequest: SettingChangeRequest
        get() = SettingsFetchRequest()

    override fun getAsLiveData(params: SettingChangeRequest): LiveData<UserSettingEntity> = userSettings

    override fun makeAction(params: SettingChangeRequest) {
        repositoryScope.launch {
            updatingStates.value = updatingStates.value.orEmpty() + (params.id to QueryResult.Loading(params))

            try {
                when (params) {
                    is ChangeBasicSettingsRequest -> withContext(dispatchersProvider.calculationsDispatcher) {
                        api.setBasicSettings(
                            deviceIdProvider.provide(), params.newGeneralSettings.run {
                                mapOf("nsfw" to nsfws.name, "languageCode" to languageCode)
                            }
                        )
                    }
                    is ChangeNotificationSettingRequest -> withContext(dispatchersProvider.calculationsDispatcher) {
                        api.setNotificationSettings(
                            deviceIdProvider.provide(),
                            SettingToCyberMapper.map(params.newNotificationSettings)
                        )
                    }
                }
                userSettings.value = withContext(dispatchersProvider.calculationsDispatcher) {
                    toEntityMapper.map(api.getSettings(deviceIdProvider.provide()))
                }
                updatingStates.value = updatingStates.value.orEmpty() + (params.id to QueryResult.Success(params))
            } catch (e: Exception) {
                updatingStates.value = updatingStates.value.orEmpty() + (params.id to QueryResult.Error(e, params))
                Timber.e(e)
            }
        }.let { job ->
            jobsMap[params.id]?.cancel()
            jobsMap[params.id] = job
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<SettingChangeRequest>>>
        get() = updatingStates
}