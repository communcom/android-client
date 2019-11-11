package io.golos.domain.requestmodel

import io.golos.domain.dto.GeneralSettingEntity
import io.golos.domain.dto.NotificationSettingsEntity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-26.
 */
sealed class SettingChangeRequest : Identifiable

data class ChangeBasicSettingsRequest(val newGeneralSettings: GeneralSettingEntity) : SettingChangeRequest() {
    private val _id = Id()

    override val id: Identifiable.Id
        get() = _id

    inner class Id : Identifiable.Id() {
        val _newGeneralSettings = newGeneralSettings
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (_newGeneralSettings != other._newGeneralSettings) return false

            return true
        }

        override fun hashCode(): Int {
            return _newGeneralSettings.hashCode()
        }
    }
}

data class ChangeNotificationSettingRequest(val newNotificationSettings: NotificationSettingsEntity) :
    SettingChangeRequest() {
    private val _id = Id()

    inner class Id : Identifiable.Id() {
        val _newNotificationSettings = newNotificationSettings
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Id

            if (_newNotificationSettings != other._newNotificationSettings) return false

            return true
        }

        override fun hashCode(): Int {
            return _newNotificationSettings.hashCode()
        }

    }

    override val id: Identifiable.Id
        get() = _id
}

data class SettingsFetchRequest(val str: String = "#dont touch this#") : SettingChangeRequest() {
    inner class Id : Identifiable.Id()

    private val _id = Id()

    override val id: Identifiable.Id
        get() = _id
}