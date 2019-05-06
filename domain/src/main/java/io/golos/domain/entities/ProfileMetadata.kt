package io.golos.domain.entities

import io.golos.cyber4j.model.CyberName
import io.golos.domain.Entity
import java.util.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
data class UserMetadataCollectionEntity(val map: Map<CyberName, UserMetadataEntity>) : Entity

data class UserMetadataEntity(
    val personal: UserPersonalDataEntity,
    val subscriptions: UserSubscriptionsEntity,
    val stats: UserStatsEntity,
    val userId: CyberName,
    val username: String,
    val subscribers: SubscribersEntity,
    val createdAt: Date,
    val isSubscribed: Boolean
) : Entity

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