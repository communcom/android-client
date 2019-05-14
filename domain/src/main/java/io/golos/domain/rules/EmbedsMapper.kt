package io.golos.domain.rules


import io.golos.cyber4j.services.model.IFramelyEmbedResult
import io.golos.cyber4j.services.model.OEmbedResult
import io.golos.domain.entities.LinkEmbedResult

/**
 * Created by yuri yurivladdurain@gmail.com on 2019-04-01.
 */

data class IFramelyEmbedResultRelatedData(val iframelyResult: IFramelyEmbedResult, val originalRequestUrl: String)


class IfremlyEmbedMapper : CyberToEntityMapper<IFramelyEmbedResultRelatedData, LinkEmbedResult> {
    private val typeHtmlText = "text/html"
    private val typeImage = "image"
    private val xIcon = "image/x-icon"
    private val imageJpeg = "image/jpeg"
    private val imagePng = "image/png"
    private val imageIcon = "image/icon"
    private val svgImage = "image/x-icon"
    private val image = "image"


    override suspend fun invoke(cyberObject: IFramelyEmbedResultRelatedData): LinkEmbedResult {
        val url = cyberObject.originalRequestUrl
        val embedData = cyberObject.iframelyResult

        val firstThumbNail = embedData.links?.thumbnail?.firstOrNull()
        val videoHtml = embedData.links?.player?.firstOrNull()?.html
        val appHtml = embedData.links?.app?.firstOrNull()?.html
        val readerHtml = embedData.links?.reader?.firstOrNull()?.html
        val imageHtml = embedData.links?.image?.firstOrNull()?.html

        return LinkEmbedResult(
            embedData.meta?.title ?: embedData.meta?.description ?: "",
            embedData.meta.site ?: "",
            embedData.meta?.canonical.orEmpty(),
            videoHtml ?: appHtml ?: readerHtml ?: imageHtml ?: embedData.html ?: "",
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
            "",
            embedData.url,
            url,
            embedData.thumbnail_url,
            embedData.thumbnail_height to embedData.thumbnail_width
        )
    }
}
