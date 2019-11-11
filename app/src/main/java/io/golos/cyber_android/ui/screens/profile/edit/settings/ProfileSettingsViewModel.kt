package io.golos.cyber_android.ui.screens.profile.edit.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.golos.commun4j.services.model.GetProfileResult
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.keys_to_pdf.StartExportingCommand
import io.golos.cyber_android.ui.common.mvvm.SingleLiveData
import io.golos.cyber_android.ui.common.mvvm.view_commands.SetLoadingVisibilityCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ShowMessageCommand
import io.golos.cyber_android.ui.common.mvvm.view_commands.ViewCommand
import io.golos.cyber_android.ui.screens.profile.edit.settings.language.LanguageOption
import io.golos.cyber_android.ui.screens.profile.edit.settings.notifications.NotificationSetting
import io.golos.cyber_android.ui.screens.profile.edit.settings.notifications.toSettingsEntity
import io.golos.cyber_android.ui.screens.profile.edit.settings.notifications.toSettingsList
import io.golos.cyber_android.utils.asEvent
import io.golos.data.api.user_metadata.UserMetadataApi
import io.golos.domain.DispatchersProvider
import io.golos.domain.KeyValueStorageFacade
import io.golos.domain.UserKeyStore
import io.golos.domain.dto.NSFWSettingsEntity
import io.golos.domain.dto.UserKey
import io.golos.domain.dto.UserKeyType
import io.golos.domain.extensions.map
import io.golos.domain.use_cases.notifs.push.PushNotificationsSettingsUseCase
import io.golos.domain.use_cases.settings.SettingsUseCase
import io.golos.domain.use_cases.sign.SignInUseCase
import io.golos.domain.requestmodel.ChangeBasicSettingsRequestModel
import io.golos.domain.requestmodel.ChangeNotificationSettingRequestModel
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ProfileSettingsViewModel
@Inject
constructor(
    private val settingsUseCase: SettingsUseCase,
    private val pushesUseCaes: PushNotificationsSettingsUseCase,
    private val signInUseCase: SignInUseCase,
    private val dispatchersProvider: DispatchersProvider,
    private val metadadataApi: UserMetadataApi,
    private val userKeyStore: UserKeyStore,
    private val keyValueStorage: KeyValueStorageFacade
) : ViewModel(), CoroutineScope {

    private val scopeJob: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = scopeJob + dispatchersProvider.uiDispatcher

    /**
     * [LiveData] for users notification settings
     */
    val getNotificationSettingsLiveData = settingsUseCase.getAsLiveData
        .map { it?.notifsSettings?.toSettingsList() }

    /**
     * [LiveData] for users push notification settings
     */
    val getPushNotificationsSettingsLiveData = pushesUseCaes.getAsLiveData

    /**
     * [LiveData] for users general settings
     */
    val getGeneralSettingsLiveData = settingsUseCase.getAsLiveData
        .map { it?.general }

    /**
     * [LiveData] for update state
     */
    val getUpdateState = settingsUseCase.getUpdateState

    val getReadinessLiveData = settingsUseCase.getSettingsReadiness.asEvent()

    val command: SingleLiveData<ViewCommand> = SingleLiveData()

    init {
        settingsUseCase.subscribe()
        signInUseCase.subscribe()
        pushesUseCaes.subscribe()
    }

    fun onNotificationSettingChanged(item: NotificationSetting, isEnabled: Boolean) {
        getNotificationSettingsLiveData.value?.let { settings ->
            settingsUseCase.makeAction(
                ChangeNotificationSettingRequestModel(
                    settings.apply {
                        find { it == item }?.isEnabled = isEnabled
                    }.toSettingsEntity()
                )
            )
        }
    }

    /**
     * Changes selected NSFW option
     */
    fun onNSFWOptionSelected(item: NSFWSettingsEntity) {
        getGeneralSettingsLiveData.value?.let { settings ->
            settingsUseCase.makeAction(
                ChangeBasicSettingsRequestModel(settings.copy(nsfws = item))
            )
        }
    }

    /**
     * Changes selected language
     */
    fun onLanguageOptionSelected(item: LanguageOption) {
        getGeneralSettingsLiveData.value?.let { settings ->
            settingsUseCase.makeAction(
                ChangeBasicSettingsRequestModel(settings.copy(languageCode = item.code))
            )
        }
    }


    fun onPushSettingsSelected(toEnable: Boolean) {
        if (toEnable) pushesUseCaes.subscribeToPushNotifications()
        else pushesUseCaes.unsubscribeFromPushNotifications()
    }

    fun logOut() {
        signInUseCase.logOut()
    }

    fun onExportPathSelected() {
        launch {
            command.value = SetLoadingVisibilityCommand(true)

            try {
                val metadata = getUserMetadata()
                val keys = getAllKeys()

                command.value = SetLoadingVisibilityCommand(false)
                command.value = StartExportingCommand(
                    metadata.username!!,
                    metadata.userId.name,
                    keys
                )
            } catch (ex: Exception) {
                Timber.e(ex)
                command.value = SetLoadingVisibilityCommand(false)
                command.value = ShowMessageCommand(R.string.common_general_error)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        scopeJob.takeIf { it.isActive }?.cancel()

        settingsUseCase.unsubscribe()
        signInUseCase.unsubscribe()
        pushesUseCaes.unsubscribe()
    }

    private suspend fun getAllKeys(): List<UserKey> =
        withContext(dispatchersProvider.ioDispatcher) {
            listOf(
                UserKeyType.MASTER,
                UserKeyType.OWNER,
                UserKeyType.ACTIVE,
                UserKeyType.POSTING,
                UserKeyType.MEMO
            )
                .map { keyType ->
                    UserKey(keyType, userKeyStore.getKey(keyType))
                }
        }

    private suspend fun getUserMetadata(): GetProfileResult =
        withContext(dispatchersProvider.ioDispatcher) {
            metadadataApi.getUserMetadata(keyValueStorage.getAuthState()!!.user).profile
        }
}