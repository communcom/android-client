package io.golos.domain.dto.notifications

import io.golos.domain.dto.ContentIdDomain

data class NotificationCommentParentsDomain(val post: ContentIdDomain, val comment: ContentIdDomain?)