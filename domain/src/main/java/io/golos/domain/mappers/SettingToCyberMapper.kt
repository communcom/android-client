package io.golos.domain.mappers

import io.golos.commun4j.services.model.MobileShowSettings
import io.golos.commun4j.services.model.NotificationSettings
import io.golos.commun4j.services.model.ServiceSettingsLanguage
import io.golos.domain.dto.NotificationSettingsEntity
import java.util.*

object SettingToCyberMapper {
    fun map(entity: NotificationSettingsEntity): MobileShowSettings {
        return MobileShowSettings(
            NotificationSettings(
                entity.showUpvote, entity.showDownvote,
                entity.showReply, entity.showTransfer, entity.showSubscribe, entity.showUnsubscribe,
                entity.showMention, entity.showRepost, entity.showWitnessVote,
                entity.showWitnessCancelVote, entity.showReward, entity.showCuratorReward
            ), getLanguge()
        )
    }

    private fun getLanguge(): ServiceSettingsLanguage {
        return if ((Locale.getDefault()?.language ?: "en").contains("ru")) ServiceSettingsLanguage.RUSSIAN
        else ServiceSettingsLanguage.ENGLISH
    }
}