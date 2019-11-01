package io.golos.domain.mappers

import io.golos.domain.entities.LinkEmbedResult

object OembedMapper {
    fun map(communObject: OembedResultRelatedData?): LinkEmbedResult {
        val url = communObject?.originalRequestUrl
        val embedData = communObject?.oembedResult

        return LinkEmbedResult(
            embedData!!.description,
            embedData!!.provider_name,
            "",
            embedData!!.url,
            url!!,
            embedData!!.thumbnail_url,
            (embedData?.thumbnail_height ?:0) to (embedData?.thumbnail_width ?:0)
        )
    }
}
