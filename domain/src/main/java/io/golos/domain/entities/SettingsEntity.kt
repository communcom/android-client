package io.golos.domain.entities

import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-26.
 */
enum class NSFWSettingsEntity : Entity {
    ALWAYS_SHOW, ALERT_WARN, ALWAYS_HIDE
}

data class GeneralSettingEntity(
    val nsfws: NSFWSettingsEntity,
    val languageCode: String
) : Entity

data class NotificationSettingsEntity(
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
) : Entity

data class UserSettingEntity(
    val general: GeneralSettingEntity,
    val notifsSettings: NotificationSettingsEntity
) : Entity