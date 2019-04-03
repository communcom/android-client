package io.golos.domain.entities

import io.golos.domain.Entity

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */
data class LinkEmbedResult(
    val summary: String,
    val provider: String,
    val url: String,
    val embeddedHtml: String,
    val originalQueryUrl: String,
    val thumbnailImageUrl: String,
    /*h*w*/ val thumbnailSize: Pair<Int, Int>
) : Entity

data class ProcessedLinksEntity(val embeds: Set<LinkEmbedResult>) : Entity