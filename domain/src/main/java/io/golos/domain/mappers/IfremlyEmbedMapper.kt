package io.golos.domain.mappers

import io.golos.domain.entities.LinkEmbedResult
import javax.inject.Inject

class IfremlyEmbedMapper
@Inject
constructor() :
    CommunToEntityMapper<IFramelyEmbedResultRelatedData, LinkEmbedResult> {
    private val typeHtmlText = "text/html"
    private val typeImage = "image"
    private val xIcon = "image/x-icon"
    private val imageJpeg = "image/jpeg"
    private val imagePng = "image/png"
    private val imageIcon = "image/icon"
    private val svgImage = "image/x-icon"
    private val image = "image"


    override suspend fun map(communObject: IFramelyEmbedResultRelatedData): LinkEmbedResult {
        val url = communObject.originalRequestUrl
        val embedData = communObject.iframelyResult

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