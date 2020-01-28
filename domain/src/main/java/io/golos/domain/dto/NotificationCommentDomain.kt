package io.golos.domain.dto

data class NotificationCommentDomain(val contentId: ContentIdDomain,
                                     val shortText: String?,
                                     val imageUrl: String?,
                                     val parents: NotificationCommentParentsDomain
)