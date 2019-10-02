package io.golos.posts_parsing_rendering.json_to_dto

import android.util.Log
import io.golos.cyber4j.sharedmodel.Either
import io.golos.domain.Logger
import io.golos.domain.post.post_dto.*
import io.golos.posts_parsing_rendering.json_to_dto.mappers.MappersFactory
import io.golos.posts_parsing_rendering.json_to_dto.mappers.PostMapper
import io.golos.posts_parsing_rendering.json_to_dto.mappers.PostMetadataMapper
import org.json.JSONException
import org.json.JSONObject

class JsonToDtoMapper(private val logger: Logger?) {
    private val mappersFactory = MappersFactory()

    @Suppress("RemoveExplicitTypeArguments")
    fun map(rawJson: String): Either<PostBlock, JsonMappingErrorCode> =
        try {
            val post = mappersFactory.getMapper<PostMapper>(PostMapper::class).map(JSONObject(rawJson))
            Either.Success<PostBlock, JsonMappingErrorCode>(post)
        } catch (ex: IncompatibleVersionsException) {
            logger?.log(ex)
            Either.Failure<PostBlock, JsonMappingErrorCode>(JsonMappingErrorCode.INCOMPATIBLE_VERSIONS)
        } catch (ex: JSONException) {
            logger?.log(ex)
            Either.Failure<PostBlock, JsonMappingErrorCode>(JsonMappingErrorCode.JSON)
        }
        catch (ex: Exception) {
            logger?.log(ex)
            Either.Failure<PostBlock, JsonMappingErrorCode>(JsonMappingErrorCode.GENERAL)
        }

    /**
     * Fast way to get post metadata only
     */
    fun getPostMetadata(rawJson: String): PostMetadata = PostMetadataMapper().map(JSONObject(rawJson))
}