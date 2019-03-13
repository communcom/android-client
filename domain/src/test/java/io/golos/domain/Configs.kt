package io.golos.domain

import io.golos.domain.rules.*

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-13.
 */
val postMapper = PostMapper()
val feedMapper = PostsFeedMapper(postMapper)

val postMerger = PostMerger()
val feedMerger = PostFeedMerger()

val emptyPostFeedProducer = EmptyPostFeedProducer()

val logger = TestLogger()