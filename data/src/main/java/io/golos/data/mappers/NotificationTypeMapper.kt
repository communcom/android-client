package io.golos.data.mappers

import io.golos.commun4j.services.model.NotificationType
import io.golos.domain.dto.notifications.NotificationTypeDomain

fun NotificationType.mapToNotificationTypeDomain() =
    when(this) {
        NotificationType.SUBSCRIBE -> NotificationTypeDomain.SUBSCRIBE
        NotificationType.UPVOTE -> NotificationTypeDomain.UP_VOTE
        NotificationType.REPLY -> NotificationTypeDomain.REPLY
        NotificationType.MENTION -> NotificationTypeDomain.MENTION
        NotificationType.TRANSFER -> NotificationTypeDomain.TRANSFER
        NotificationType.REWARD -> NotificationTypeDomain.REWARD
        NotificationType.REFERRAL_REG_BONUS -> NotificationTypeDomain.REFERRAL_REG_BONUS
        NotificationType.REFERRAL_PURCH_BONUS -> NotificationTypeDomain.REFERRAL_PURCHASE_BONUS
        NotificationType.DONATION -> NotificationTypeDomain.DONATION
    }

fun NotificationTypeDomain.mapToNotificationType() =
    when(this) {
        NotificationTypeDomain.SUBSCRIBE -> NotificationType.SUBSCRIBE
        NotificationTypeDomain.UP_VOTE -> NotificationType.UPVOTE
        NotificationTypeDomain.REPLY -> NotificationType.REPLY
        NotificationTypeDomain.MENTION -> NotificationType.MENTION
        NotificationTypeDomain.TRANSFER -> NotificationType.TRANSFER
        NotificationTypeDomain.REWARD -> NotificationType.REWARD
        NotificationTypeDomain.REFERRAL_REG_BONUS -> NotificationType.REFERRAL_REG_BONUS
        NotificationTypeDomain.REFERRAL_PURCHASE_BONUS -> NotificationType.REFERRAL_PURCH_BONUS
        NotificationTypeDomain.DONATION -> NotificationType.DONATION
    }
