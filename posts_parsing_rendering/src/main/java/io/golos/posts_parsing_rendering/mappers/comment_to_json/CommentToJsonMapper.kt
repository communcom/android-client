package io.golos.posts_parsing_rendering.mappers.comment_to_json

import io.golos.posts_parsing_rendering.Attribute
import io.golos.posts_parsing_rendering.BlockType
import io.golos.posts_parsing_rendering.PostGlobalConstants
import io.golos.posts_parsing_rendering.PostTypeJson
import io.golos.posts_parsing_rendering.json_builder.JsonBuilderImpl
import io.golos.posts_parsing_rendering.json_builder.PostAttribute

object CommentToJsonMapper {
    /**
     * Text only
     */
    fun mapText(text: String):String {
        val builder = JsonBuilderImpl.create()

        builder.putBlock(
            BlockType.POST,
            true,
            PostAttribute(Attribute.VERSION, PostGlobalConstants.postFormatVersion.toString()),
            PostAttribute(Attribute.TYPE, PostTypeJson.COMMENT)) {

            builder.putBlock(BlockType.PARAGRAPH, true) {
                builder.putBlock(BlockType.TEXT, true, text)
            }
        }

        return builder.build()
    }

    /**
     * Image only
     */
    fun mapImage(imageUri: String):String {
        val builder = JsonBuilderImpl.create()

        builder.putBlock(
            BlockType.POST,
            true,
            PostAttribute(Attribute.VERSION, PostGlobalConstants.postFormatVersion.toString()),
            PostAttribute(Attribute.TYPE, PostTypeJson.COMMENT)) {

            builder.putBlock(BlockType.IMAGE, true, imageUri)
        }

        return builder.build()
    }
}