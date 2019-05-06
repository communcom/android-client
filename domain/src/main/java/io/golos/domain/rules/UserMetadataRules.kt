package io.golos.domain.rules

import io.golos.cyber4j.model.UserMetadata
import io.golos.domain.entities.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-30.
 */
class UserMetadataToEntityMapper : CyberToEntityMapper<UserMetadata, UserMetadataEntity> {
    override suspend fun invoke(cyberObject: UserMetadata): UserMetadataEntity {
        return UserMetadataEntity(
            UserPersonalDataEntity(
                cyberObject.personal?.avatarUrl,
                cyberObject.personal?.coverUrl,
                cyberObject.personal?.biography,
                ContactsEntity(
                    cyberObject.personal?.contacts?.facebook, cyberObject.personal?.contacts?.telegram,
                    cyberObject.personal?.contacts?.whatsApp, cyberObject.personal?.contacts?.weChat
                )
            ),
            UserSubscriptionsEntity(
                cyberObject.subscriptions?.usersCount ?: 0,
                cyberObject.subscriptions?.communitiesCount ?: 0
            ),
            UserStatsEntity(cyberObject.stats?.postsCount ?: 0, cyberObject.stats?.commentsCount ?: 0),
            cyberObject.userId,
            cyberObject.username,
            SubscribersEntity(
                cyberObject.subscribers?.usersCount ?: 0,
                cyberObject.subscribers?.communitiesCount ?: 0
            ),
            cyberObject.createdAt,
            cyberObject.isSubscribed
        )
    }
}