package io.golos.domain.mappers.new_mappers

import io.golos.commun4j.model.DiscussionAuthor
import io.golos.domain.dto.CyberUser
import io.golos.domain.use_cases.model.DiscussionAuthorModel

fun DiscussionAuthor.map(): DiscussionAuthorModel =
    DiscussionAuthorModel(CyberUser(this.userId.name), this.username!!, this.avatarUrl)
