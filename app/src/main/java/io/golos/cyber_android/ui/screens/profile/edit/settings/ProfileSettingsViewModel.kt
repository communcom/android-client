package io.golos.cyber_android.ui.screens.profile.edit.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.golos.cyber_android.ui.screens.profile.edit.settings.language.LanguageOption
import io.golos.cyber_android.ui.screens.profile.edit.settings.notifications.NotificationSetting
import io.golos.cyber_android.ui.screens.profile.edit.settings.notifications.toSettingsEntity
import io.golos.cyber_android.ui.screens.profile.edit.settings.notifications.toSettingsList
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.entities.NSFWSettingsEntity
import io.golos.domain.interactors.settings.SettingsUseCase
import io.golos.domain.interactors.sign.SignInUseCase
import io.golos.domain.map
import io.golos.domain.requestmodel.ChangeBasicSettingsRequestModel
import io.golos.domain.requestmodel.ChangeNotificationSettingRequestModel

class ProfileSettingsViewModel(private val settingsUseCase: SettingsUseCase,
                               private val signInUseCase: SignInUseCase
) : ViewModel() {

    /**
     * [LiveData] for users notification settings
     */
    val getNotificationSettingsLiveData = settingsUseCase.getAsLiveData
        .map { it?.notifsSettings?.toSettingsList() }

    /**
     * [LiveData] for users general settings
     */
    val getGeneralSettingsLiveData = settingsUseCase.getAsLiveData
        .map { it?.general }

    val getReadinessLiveData = settingsUseCase.getSettingsReadiness.asEvent()

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

    fun logOut() {
        signInUseCase.logOut()
    }

    init {
        settingsUseCase.subscribe()
        signInUseCase.subscribe()
    }

    override fun onCleared() {
        super.onCleared()
        settingsUseCase.unsubscribe()
        signInUseCase.unsubscribe()
    }
}