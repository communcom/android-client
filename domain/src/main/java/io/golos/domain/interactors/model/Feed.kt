package io.golos.domain.interactors.model

import io.golos.domain.Model

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-03-18.
 */
sealed class Feed<T>(open val pageId: String?, open val items: T) : Model

data class PostFeed(
    override val pageId: String?,
    override val items: List<PostModel>
) : Feed<List<PostModel>>(pageId, items)