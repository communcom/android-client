package io.golos.posts_parsing_rendering.json_to_dto

import io.golos.cyber4j.sharedmodel.Either
import io.golos.domain.Logger
import io.golos.domain.post.post_dto.*
import io.golos.posts_parsing_rendering.Attribute
import io.golos.posts_parsing_rendering.BlockType
import io.golos.posts_parsing_rendering.GlobalConstants
import io.golos.posts_parsing_rendering.json_to_dto.mappers.MappersFactory
import io.golos.posts_parsing_rendering.json_to_dto.mappers.PostMapper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.UnsupportedOperationException

class JsonToDtoMapper(private val logger: Logger) {
    private val mappersFactory = MappersFactory()

    fun map(rawJson: String): Either<PostBlock, JsonMappingErrorCode> =
        try {
            val post = mappersFactory.getMapper<PostMapper>(PostMapper::class).map(JSONObject(rawJson))
            Either.Success<PostBlock, JsonMappingErrorCode>(post)
        } catch (ex: IncompatibleVersionsException) {
            logger.log(ex)
            Either.Failure<PostBlock, JsonMappingErrorCode>(JsonMappingErrorCode.INCOMPATIBLE_VERSIONS)
        } catch (ex: JSONException) {
            logger.log(ex)
            Either.Failure<PostBlock, JsonMappingErrorCode>(JsonMappingErrorCode.JSON)
        }
        catch (ex: Exception) {
            logger.log(ex)
            Either.Failure<PostBlock, JsonMappingErrorCode>(JsonMappingErrorCode.GENERAL)
        }
}