package io.golos.domain.requestmodel

import io.golos.domain.Model
import io.golos.domain.dto.NSFWSettingsEntity
import io.golos.domain.dto.NotificationSettingsEntity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-26.
 */
sealed class SettingChangeRequestModel : Model

data class ChangeBasicSettingsRequestModel(val newGeneralSettings: GeneralSettingsModel) : SettingChangeRequestModel()

data class ChangeNotificationSettingRequestModel(val newNotificationSettings: NotificationSettingsEntity) :
    SettingChangeRequestModel()

data class SettingsFetchRequestModel(val str: String = "#dont touch this#") : SettingChangeRequestModel()

data class GeneralSettingsModel(
    val nsfws: NSFWSettingsEntity,
    val languageCode: String
) : Model

data class NotificationSettingsModel(
    val showUpvote: Boolean,
    val showDownvote: Boolean,
    val showReply: Boolean,
    val showTransfer: Boolean,
    val showSubscribe: Boolean,
    val showUnsubscribe: Boolean,
    val showMention: Boolean,
    val showRepost: Boolean,
    val showWitnessVote: Boolean,
    val showWitnessCancelVote: Boolean,
    val showReward: Boolean,
    val showCuratorReward: Boolean
) : Model

data class UserSettingModel(
    val general: GeneralSettingsModel,
    val notifsSettings: NotificationSettingsModel
) : Model
