package io.golos.domain.use_cases.model

import io.golos.domain.Model

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
sealed class Feed<T>(
    open val items: T
) : Model

data class DiscussionsFeed<M : DiscussionModel>(
    override val items: List<M>
) : Feed<List<M>>(items)