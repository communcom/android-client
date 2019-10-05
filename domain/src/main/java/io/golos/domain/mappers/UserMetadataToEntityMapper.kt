package io.golos.domain.mappers

import io.golos.commun4j.services.model.GetProfileResult
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.domain.entities.UserMetadataEntity
import io.golos.domain.entities.UserPersonalDataEntity

import io.golos.domain.entities.*
import io.golos.domain.mappers.CommunToEntityMapper
import javax.inject.Inject

// todo[AS] temporary stub to compile!!!
class UserMetadataToEntityMapper
@Inject
constructor() : CommunToEntityMapper<GetProfileResult, UserMetadataEntity> {
    override suspend fun map(communObject: GetProfileResult): UserMetadataEntity {
        return UserMetadataEntity(
            UserPersonalDataEntity(
                "",
                "",
                "",
                ContactsEntity(
                    "", "", "", "")
            ),
            UserSubscriptionsEntity(0, 0),
            UserStatsEntity(communObject.stats.postsCount, communObject.stats.commentsCount),
            CyberName(""),
            communObject.username!!,
            SubscribersEntity(communObject.subscribers.usersCount, communObject.subscribers.communitiesCount),
            communObject.registration.time,
            false
        )
    }
}

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-30.
 */
//class UserMetadataToEntityMapper
//@Inject
//constructor() : CyberToEntityMapper<UserMetadataResult, UserMetadataEntity> {
//    override suspend fun invoke(cyberObject: UserMetadataResult): UserMetadataEntity {
//        return UserMetadataEntity(
//            UserPersonalDataEntity(
//                cyberObject.personal?.avatarUrl,
//                cyberObject.personal?.coverUrl,
//                cyberObject.personal?.about,
//                ContactsEntity(
//                    cyberObject.personal?.contacts?.facebook, cyberObject.personal?.contacts?.telegram,
//                    cyberObject.personal?.contacts?.whatsApp, cyberObject.personal?.contacts?.weChat
//                )
//            ),
//            UserSubscriptionsEntity(
//                cyberObject.subscriptions?.usersCount ?: 0,
//                cyberObject.subscriptions?.communitiesCount ?: 0
//            ),
//            UserStatsEntity(cyberObject.stats?.postsCount ?: 0, cyberObject.stats?.commentsCount ?: 0),
//            cyberObject.userId,
//            cyberObject.username,
//            SubscribersEntity(
//                cyberObject.subscribers?.usersCount ?: 0,
//                cyberObject.subscribers?.communitiesCount ?: 0
//            ),
//            cyberObject.createdAt,
//            cyberObject.isSubscribed
//        )
//    }
//}
