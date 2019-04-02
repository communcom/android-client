package io.golos.domain.rules

import io.golos.cyber4j.model.IFramelyEmbedResult
import io.golos.cyber4j.model.OEmbedResult
import io.golos.domain.entities.LinkEmbedResult

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */

data class IFramelyEmbedResultRelatedData(val iframelyResult: IFramelyEmbedResult, val originalRequestUrl: String)


class IfremlyEmbedMapper : CyberToEntityMapper<IFramelyEmbedResultRelatedData, LinkEmbedResult> {
    override suspend fun invoke(cyberObject: IFramelyEmbedResultRelatedData): LinkEmbedResult {
        val url = cyberObject.originalRequestUrl
        val embedData = cyberObject.iframelyResult
        val firstThumbNail = embedData.links?.thumbnail?.firstOrNull()
        return LinkEmbedResult(
            embedData.meta?.title ?: embedData.meta?.description ?: "",
            embedData.meta.site?:"",
            embedData.meta?.canonical.orEmpty(),
            url,
            firstThumbNail?.href ?: "",
            (firstThumbNail?.media?.height ?: 0) to (firstThumbNail?.media?.width ?: 0)
        )
    }
}

data class OembedResultRelatedData(val oembedResult: OEmbedResult, val originalRequestUrl: String)

class OembedMapper : CyberToEntityMapper<OembedResultRelatedData, LinkEmbedResult> {
    override suspend fun invoke(cyberObject: OembedResultRelatedData): LinkEmbedResult {
        val url = cyberObject.originalRequestUrl
        val embedData = cyberObject.oembedResult

        return LinkEmbedResult(
            embedData.description, embedData.provider_name,
            embedData.url,
            url,
            embedData.thumbnail_url,
            embedData.thumbnail_height to embedData.thumbnail_width
        )
    }
}
