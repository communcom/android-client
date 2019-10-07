package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto

import io.golos.commun4j.sharedmodel.Either
import io.golos.domain.Logger
import io.golos.domain.post.post_dto.PostBlock
import io.golos.domain.post.post_dto.PostMetadata
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers.MappersFactory
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers.PostMapper
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers.PostMetadataMapper
import org.json.JSONException
import org.json.JSONObject

class JsonToDtoMapper(private val logger: Logger?) {
    private val mappersFactory = MappersFactory()

    @Suppress("RemoveExplicitTypeArguments")
    fun map(rawJson: String): PostBlock =
        try {
            mappersFactory.getMapper<PostMapper>(PostMapper::class).map(JSONObject(rawJson))
        } catch (ex: IncompatibleVersionsException) {
            logger?.log(ex)
            throw PostParsingException(PostParsingErrorCode.INCOMPATIBLE_VERSIONS)
        } catch (ex: JSONException) {
            logger?.log(ex)
            throw PostParsingException(PostParsingErrorCode.JSON)
        }
        catch (ex: Exception) {
            logger?.log(ex)
            throw PostParsingException(PostParsingErrorCode.GENERAL)
        }

    /**
     * Fast way to get post metadata only
     */
    fun getPostMetadata(rawJson: String): PostMetadata = PostMetadataMapper().map(JSONObject(rawJson))
}