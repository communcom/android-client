package io.golos.data.mappers

import io.golos.commun4j.services.model.NotificationEntityContent
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.NotificationCommentDomain
import io.golos.domain.dto.NotificationCommentParentsDomain

fun NotificationEntityContent.mapToNotificationCommentDomain(): NotificationCommentDomain {
    val commentContentId = ContentIdDomain(contentId.communityId?.value ?: "", this.contentId.permlink, contentId.userId.name)
    val entityContentParents = parents!!
    val parentPostContentId = ContentIdDomain(entityContentParents.post!!.communityId!!.value, entityContentParents.post!!.permlink, entityContentParents.post!!.username!!)
    val parentCommentContentId = entityContentParents.comment?.let {
        ContentIdDomain(it.communityId!!.value, it.permlink, it.username!!)
    }
    val parentsDomain = NotificationCommentParentsDomain(parentPostContentId, parentCommentContentId)
    return NotificationCommentDomain(commentContentId, shortText, imageUrl, parentsDomain)
}