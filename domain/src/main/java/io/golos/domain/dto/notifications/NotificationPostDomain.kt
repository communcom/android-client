package io.golos.domain.dto.notifications

import io.golos.domain.dto.ContentIdDomain


data class NotificationPostDomain(val contentId: ContentIdDomain, val shortText: String?, val imageUrl: String?)