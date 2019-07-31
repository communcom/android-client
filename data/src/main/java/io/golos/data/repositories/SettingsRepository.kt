package io.golos.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.golos.cyber4j.services.model.MobileShowSettings
import io.golos.cyber4j.services.model.UserSettings
import io.golos.data.api.SettingsApi
import io.golos.domain.*
import io.golos.domain.entities.NotificationSettingsEntity
import io.golos.domain.entities.UserSettingEntity
import io.golos.domain.requestmodel.*
import io.golos.domain.rules.CyberToEntityMapper
import io.golos.domain.rules.EntityToCyberMapper
import kotlinx.coroutines.*
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
    private val toEntityMapper: CyberToEntityMapper<UserSettings, UserSettingEntity>,
    private val toCyberMapper: EntityToCyberMapper<NotificationSettingsEntity, MobileShowSettings>,
    private val dispatchersProvider: DispatchersProvider,
    private val deviceIdProvider: DeviceIdProvider,
    defaultUserSettingsProvider: DefaultSettingProvider,
    private val logger: Logger
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
                    is ChangeBasicSettingsRequest -> withContext(dispatchersProvider.calculationskDispatcher) {
                        api.setBasicSettings(
                            deviceIdProvider.provide(), params.newGeneralSettings.run {
                                mapOf("nsfw" to nsfws.name, "languageCode" to languageCode)
                            }
                        )
                    }
                    is ChangeNotificationSettingRequest -> withContext(dispatchersProvider.calculationskDispatcher) {
                        api.setNotificationSettings(
                            deviceIdProvider.provide(),
                            toCyberMapper(params.newNotificationSettings)
                        )
                    }
                }
                userSettings.value = withContext(dispatchersProvider.calculationskDispatcher) {
                    toEntityMapper(api.getSettings(deviceIdProvider.provide()))
                }
                updatingStates.value = updatingStates.value.orEmpty() + (params.id to QueryResult.Success(params))
            } catch (e: Exception) {
                updatingStates.value = updatingStates.value.orEmpty() + (params.id to QueryResult.Error(e, params))
                logger(e)
            }
        }.let { job ->
            jobsMap[params.id]?.cancel()
            jobsMap[params.id] = job
        }
    }

    override val updateStates: LiveData<Map<Identifiable.Id, QueryResult<SettingChangeRequest>>>
        get() = updatingStates
}