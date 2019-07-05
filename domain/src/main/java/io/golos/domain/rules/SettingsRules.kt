package io.golos.domain.rules

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.golos.cyber4j.services.model.MobileShowSettings
import io.golos.cyber4j.services.model.NotificationSettings
import io.golos.cyber4j.services.model.ServiceSettingsLanguage
import io.golos.cyber4j.services.model.UserSettings
import io.golos.domain.DefaultSettingProvider
import io.golos.domain.DeviceIdProvider
import io.golos.domain.entities.GeneralSettingEntity
import io.golos.domain.entities.NSFWSettingsEntity
import io.golos.domain.entities.NotificationSettingsEntity
import io.golos.domain.entities.UserSettingEntity
import java.io.File
import java.util.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-26.
 */
class MyDefaultSettingProvider() : DefaultSettingProvider {
    override fun provide(): UserSettingEntity {
        return UserSettingEntity(
            GeneralSettingEntity(NSFWSettingsEntity.ALERT_WARN, "en"),
            NotificationSettingsEntity(
                true, true, true, true, true,
                true, true, true, true, true, true,
                true
            )
        )
    }
}

@SuppressLint("HardwareIds")
class MyDeviceIdProvider(private val context: Context) : DeviceIdProvider {
    private val fileName = "device_id.txt"

    private val deviceId by lazy {
        var deviceId = Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.ANDROID_ID
        )
        if (deviceId == null) File(context.filesDir, "device_id.txt")?.bufferedReader()?.readLine()

        if (deviceId.isNullOrEmpty()) {
            deviceId = UUID.randomUUID().toString()
            val idFile = File(context.filesDir, "device_id.txt")
            idFile.bufferedWriter().apply {
                write(deviceId)
                flush()
            }

        }

        deviceId
    }

    override fun provide(): String {
        return deviceId
    }
}

class SettingsToEntityMapper(private val moshi: Moshi) : CyberToEntityMapper<UserSettings, UserSettingEntity> {
    override suspend fun invoke(cyberObject: UserSettings): UserSettingEntity {
        val push = cyberObject.push?.show
        val mapType = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        val setting = moshi.adapter<Map<String, String>>(mapType).fromJsonValue(cyberObject.basic ?: "{}").orEmpty()
            .run {
                GeneralSettingEntity(
                    this["nsfw"]?.run {
                        try {
                            NSFWSettingsEntity.valueOf(this)
                        } catch (e: Exception) {
                            NSFWSettingsEntity.ALERT_WARN
                        }
                    } ?: NSFWSettingsEntity.ALERT_WARN,
                    this["languageCode"] ?: "en"
                )
            }

        return UserSettingEntity(
            setting,
            NotificationSettingsEntity(
                push?.upvote.orTrue(),
                push?.downvote.orTrue(),
                push?.reply.orTrue(),
                push?.transfer.orTrue(),
                push?.subscribe.orTrue(),
                push?.unsubscribe.orTrue(),
                push?.mention.orTrue(),
                push?.repost.orTrue(),
                push?.witnessVote.orTrue(),
                push?.witnessCancelVote.orTrue(),
                push?.reward.orTrue(),
                push?.curatorReward.orTrue()
            )
        )
    }

    private fun Boolean?.orTrue(): Boolean = this ?: true
}

class SettingToCyberMapper : EntityToCyberMapper<NotificationSettingsEntity, MobileShowSettings> {
    override suspend fun invoke(entity: NotificationSettingsEntity): MobileShowSettings {
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