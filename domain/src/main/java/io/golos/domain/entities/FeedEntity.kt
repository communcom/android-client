package io.golos.domain.entities

import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 11/03/2019.
 */
data class FeedEntity<T : Entity>(
    val discussions: List<T>,
    val pageId: String?,
    val nextPageId: String
) : Entity

data class FeedRelatedData<T : Entity>(
    val feed: FeedEntity<T>,
    val fixedPositionEntities: Set<T>
) : Entity


