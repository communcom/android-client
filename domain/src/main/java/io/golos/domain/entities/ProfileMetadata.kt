package io.golos.domain.entities

import io.golos.cyber4j.sharedmodel.CyberName
import io.golos.domain.Entity
import java.util.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
data class UserMetadataCollectionEntity(val map: Map<CyberName, UserMetadataEntity>) : Entity,
    Map<CyberName, UserMetadataEntity> by map

data class UserMetadataEntity(
    val personal: UserPersonalDataEntity,
    val subscriptions: UserSubscriptionsEntity,
    val stats: UserStatsEntity,
    val userId: CyberName,
    val username: String,
    val subscribers: SubscribersEntity,
    val createdAt: Date,
    val isSubscribed: Boolean
) : Entity {
    companion object {
        val empty: UserMetadataEntity
            get() = UserMetadataEntity(
                UserPersonalDataEntity("", "", "", ContactsEntity("", "", "", "")),
                UserSubscriptionsEntity(0, 0),
                UserStatsEntity(0L, 0L),
                CyberName(""),
                "",
                SubscribersEntity(0, 0),
                Date(), false


            )
    }
}

data class SubscribersEntity(
    val usersCount: Int,
    val communitiesCount: Int
) : Entity


data class UserPersonalDataEntity(
    val avatarUrl: String?,
    val coverUrl: String?,
    val biography: String?,
    val contacts: ContactsEntity?
) : Entity

data class ContactsEntity(val facebook: String?, val telegram: String?, val whatsApp: String?, val weChat: String?) :
    Entity

data class UserStatsEntity(val postsCount: Long, val commentsCount: Long) : Entity

data class UserSubscriptionsEntity(
    val usersCount: Int,
    val communitiesCount: Int
)