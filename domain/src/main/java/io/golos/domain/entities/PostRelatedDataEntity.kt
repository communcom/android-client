package io.golos.domain.entities

import io.golos.domain.Entity
import io.golos.domain.model.Identifiable
import io.golos.domain.model.QueryResult

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-22.
 */
data class DiscussionRelatedEntities<E : DiscussionEntity>(
    val discussionEntity: E,
    var voteStateEntity: QueryResult<VoteRequestEntity>?
) : io.golos.domain.Entity

data class FeedRelatedEntities<E : DiscussionEntity>(
    val feed: FeedEntity<E>,
    val votes: Map<Identifiable.Id, QueryResult<VoteRequestEntity>>
) : Entity