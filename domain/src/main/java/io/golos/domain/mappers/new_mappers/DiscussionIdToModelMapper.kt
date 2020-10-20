package io.golos.domain.mappers.new_mappers

import io.golos.commun4j.model.DiscussionId
import io.golos.domain.commun_entities.Permlink
import io.golos.domain.dto.CommunityIdDomain
import io.golos.domain.dto.ContentIdDomain
import io.golos.domain.dto.UserIdDomain
import io.golos.domain.use_cases.model.DiscussionIdModel

fun DiscussionId.mapToDiscussionIdModel(): DiscussionIdModel = DiscussionIdModel(this.userId.name, Permlink(this.permlink))

fun DiscussionId.mapToContentIdDomain(): ContentIdDomain =
    ContentIdDomain(
        communityId = CommunityIdDomain(this.communityId),
        permlink = this.permlink,
        userId = UserIdDomain(this.userId.name)
    )
