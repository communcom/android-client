package io.golos.domain.interactors.model

import io.golos.domain.Model
import io.golos.sharedmodel.CyberName
import java.util.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-30.
 */
data class UserMetadataModel(
    val personal: UserPersonalDataModel,
    val subscriptions: UserSubscriptionsModel,
    val stats: UserStatsModel,
    val userId: CyberName,
    val username: String,
    val subscribers: SubscribersModel,
    val createdAt: Date,
    val isSubscribed: Boolean
) : Model {
    companion object {
        val empty = UserMetadataModel(
            UserPersonalDataModel(null, null, null, null),
            UserSubscriptionsModel
                (0, 0), UserStatsModel
                (0, 0), CyberName
                (""), "", SubscribersModel
                (0, 0), Date
                (), false
        )
    }
}

data class SubscribersModel(
    val usersCount: Int,
    val communitiesCount: Int
) : Model

data class UserPersonalDataModel(
    val avatarUrl: String?,
    val coverUrl: String?,
    val biography: String?,
    val contacts: ContactsModel?
) : Model


data class ContactsModel(val facebook: String?, val telegram: String?, val whatsApp: String?, val weChat: String?) :
    Model

data class UserStatsModel(val postsCount: Long, val commentsCount: Long) : Model

data class UserSubscriptionsModel(
    val usersCount: Int,
    val communitiesCount: Int
)
