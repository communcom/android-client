package io.golos.domain.entities

import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */
data class LinkEmbedResult(
    val summary: String,
    val provider: String,
    val thumbnailImageUrl: String,
    /*h*w*/ val thumbnailSize: Pair<Int, Int>
) : Entity