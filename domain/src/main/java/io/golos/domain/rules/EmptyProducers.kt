package io.golos.domain.rules

import io.golos.domain.entities.FeedEntity
import io.golos.domain.entities.PostEntity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
class EmptyPostFeedProducer : EmptyEntityProducer<FeedEntity<PostEntity>> {
    override fun invoke(): FeedEntity<PostEntity> {
        return FeedEntity(emptyList(), null, "")
    }
}