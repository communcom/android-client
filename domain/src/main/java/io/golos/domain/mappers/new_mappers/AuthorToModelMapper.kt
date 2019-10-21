package io.golos.domain.mappers.new_mappers

import io.golos.commun4j.model.DiscussionAuthor
import io.golos.domain.entities.CyberUser
import io.golos.domain.interactors.model.DiscussionAuthorModel

fun DiscussionAuthor.map(): DiscussionAuthorModel =
    DiscussionAuthorModel(CyberUser(this.userId.name), this.username!!, this.avatarUrl)
