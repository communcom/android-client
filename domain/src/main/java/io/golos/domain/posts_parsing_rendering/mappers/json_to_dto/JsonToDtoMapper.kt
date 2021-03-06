package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto

import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers.MappersFactory
import io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers.PostMapper
import io.golos.domain.posts_parsing_rendering.post_metadata.post_dto.ContentBlock
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class JsonToDtoMapper() {
    private val mappersFactory = MappersFactory()

    @Suppress("RemoveExplicitTypeArguments")
    fun map(rawJson: String): ContentBlock =
        try {
            mappersFactory.getMapper<PostMapper>(PostMapper::class).map(JSONObject(rawJson))
        } catch (ex: IncompatibleVersionsException) {
            Timber.e(ex)
            throw PostParsingException(PostParsingErrorCode.INCOMPATIBLE_VERSIONS)
        } catch (ex: JSONException) {
            Timber.e(ex)
            throw PostParsingException(PostParsingErrorCode.JSON)
        }
        catch (ex: Exception) {
            Timber.e(ex)
            throw PostParsingException(PostParsingErrorCode.GENERAL)
        }

}