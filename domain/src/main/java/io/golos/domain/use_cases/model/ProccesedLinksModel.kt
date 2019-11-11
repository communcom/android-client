package io.golos.domain.use_cases.model

import io.golos.domain.Model
import io.golos.domain.requestmodel.QueryResult

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */
data class ProccesedLinksModel(val map: Map<String, QueryResult<LinkEmbedModel>>) : Model,
    Map<String, QueryResult<LinkEmbedModel>> by map

data class LinkEmbedModel(
    val summary: String,
    val provider: String,
    val url: String,
    val thumbnailImageUrl: String,
    val embedHtml: String,
    /*h*w*/ val thumbnailSize: Pair<Int, Int>
) : Model {

    companion object {
        val empty = LinkEmbedModel("", "", "", "", "",0 to 0)

        fun ofImage(imageUrl: String) = LinkEmbedModel("", "", "", imageUrl, "", 0 to 0)

    }
}