package io.golos.domain.mappers.new_mappers

import io.golos.commun4j.model.DiscussionAuthor
import io.golos.domain.dto.CyberUser
import io.golos.domain.dto.UserBriefDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.DiscussionAuthorModel

fun DiscussionAuthor.mapToDiscussionAuthorModel(): DiscussionAuthorModel =
    DiscussionAuthorModel(CyberUser(this.userId.name), this.username!!, this.avatarUrl)

fun DiscussionAuthor.mapToUserBriefDomain(): UserBriefDomain =
    UserBriefDomain(
        avatarUrl = this.avatarUrl,
        userId = UserIdDomain(this.userId.name),
        username = this.username
    )
