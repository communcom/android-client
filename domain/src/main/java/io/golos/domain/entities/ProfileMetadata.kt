package io.golos.domain.entities

import io.golos.cyber4j.model.CyberCommunity
import io.golos.domain.Entity
import java.util.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-20.
 */
data class UserMetadataEntity(
    val personal: UserPersonalDataEntity,
    val subscriptions: UserSubscriptionsEntity,
    val stats: UserStatsEntity,
    val userId: CyberUser,
    val username: String,
    val registration: UserRegistrationEntity
) : Entity

data class UserPersonalDataEntity(
    var avatarUrl: String?,
    var coverUrl: String?,
    var biography: String?,
    var contacts: ContactsEntity
) : Entity

data class ContactsEntity(
    var facebook: String?,
    var telegram: String?, var whatsApp: String?, var weChat: String?
) : Entity

data class UserRegistrationEntity(var time: Date) : Entity

data class UserStatsEntity(var postsCount: Long) : Entity

data class UserSubscriptionsEntity(var communities: List<CyberCommunity>) : Entity