package io.golos.domain.entities

import io.golos.domain.Entity
import io.golos.domain.model.Identifiable
import io.golos.domain.model.QueryResult

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-22.
 */
data class PostRelatedEntities(
    val postEntity: PostEntity,
    var voteStateEntity: QueryResult<VoteRequestEntity>?
) : io.golos.domain.Entity

data class FeedRelatedEntities(
    val feed: FeedEntity<PostEntity>,
    val votes: Map<Identifiable.Id, QueryResult<VoteRequestEntity>>
) : Entity