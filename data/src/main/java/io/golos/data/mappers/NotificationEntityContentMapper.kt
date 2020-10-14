package io.golos.data.mappers

import io.golos.commun4j.services.model.NotificationEntityContent
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.notifications.NotificationCommentDomain
import io.golos.domain.dto.notifications.NotificationCommentParentsDomain
import io.golos.domain.dto.notifications.NotificationPostDomain

fun NotificationEntityContent.mapToNotificationCommentDomain(): NotificationCommentDomain {
    val commentContentId = ContentIdDomain(
        CommunityIdDomain(contentId.communityId?.value ?: ""),
        this.contentId.permlink,
        contentId.userId.mapToUserIdDomain())

    val entityContentParents = parents!!
    val parentPostContentId = ContentIdDomain(
        CommunityIdDomain(entityContentParents.post!!.communityId!!.value),
        entityContentParents.post!!.permlink,
        entityContentParents.post!!.userId.mapToUserIdDomain())
    val parentCommentContentId = entityContentParents.comment?.let {
        ContentIdDomain(CommunityIdDomain(it.communityId!!.value), it.permlink, it.userId.mapToUserIdDomain())
    }
    val parentsDomain = NotificationCommentParentsDomain(
        parentPostContentId,
        parentCommentContentId
    )
    return NotificationCommentDomain(
        commentContentId,
        shortText,
        imageUrl,
        parentsDomain
    )
}


fun NotificationEntityContent.mapToNotificationPostDomain(): NotificationPostDomain {
    val commentContentId = ContentIdDomain(
        CommunityIdDomain(contentId.communityId?.value ?: ""),
        this.contentId.permlink,
        contentId.userId.mapToUserIdDomain())
    return NotificationPostDomain(commentContentId, shortText, imageUrl)
}