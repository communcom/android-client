package io.golos.domain.mappers

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.golos.commun4j.services.model.UserSettings
import io.golos.domain.entities.GeneralSettingEntity
import io.golos.domain.entities.NSFWSettingsEntity
import io.golos.domain.entities.NotificationSettingsEntity
import io.golos.domain.entities.UserSettingEntity
import timber.log.Timber
import javax.inject.Inject

interface SettingsToEntityMapper: CommunToEntityMapper<UserSettings, UserSettingEntity>

class SettingsToEntityMapperImpl
@Inject
constructor (
    private val moshi: Moshi
) : SettingsToEntityMapper {
    override fun map(communObject: UserSettings): UserSettingEntity {
        val push = communObject.push?.show
        val mapType = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        val setting = moshi.adapter<Map<String, String>>(mapType).fromJsonValue(communObject.basic ?: "{}").orEmpty()
            .run {
                GeneralSettingEntity(
                    this["nsfw"]?.run {
                        try {
                            NSFWSettingsEntity.valueOf(this)
                        } catch (e: Exception) {
                            Timber.e(e)
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