package io.golos.domain.mappers


import io.golos.domain.entities.LinkEmbedResult
import javax.inject.Inject

class OembedMapper
@Inject
constructor() : CommunToEntityMapper<OembedResultRelatedData, LinkEmbedResult> {
    override suspend fun map(communObject: OembedResultRelatedData): LinkEmbedResult {
        val url = communObject.originalRequestUrl
        val embedData = communObject.oembedResult

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
