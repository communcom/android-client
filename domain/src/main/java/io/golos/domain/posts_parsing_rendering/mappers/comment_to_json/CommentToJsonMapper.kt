package io.golos.domain.posts_parsing_rendering.mappers.comment_to_json

import io.golos.domain.posts_parsing_rendering.Attribute
import io.golos.domain.posts_parsing_rendering.BlockType
import io.golos.domain.posts_parsing_rendering.PostGlobalConstants
import io.golos.domain.posts_parsing_rendering.json_builder.JsonBuilderImpl
import io.golos.domain.posts_parsing_rendering.json_builder.PostAttribute
import io.golos.domain.posts_parsing_rendering.mappers.comment_to_json.tags.CommentTagsExtractor

object CommentToJsonMapper {
    /**
     * Text only
     */
    fun mapTextToJson(text: String): String {
//        val builder = JsonBuilderImpl.create()
//
//        builder.putBlock(
//            BlockType.POST,
//            true,
//            PostAttribute(
//                Attribute.VERSION,
//                PostGlobalConstants.postFormatVersion.toString()
//            ),
//            PostAttribute(
//                Attribute.TYPE,
//                PostTypeJson.COMMENT
//            )
//        ) {
//            val paragraphs = text
//                .split("\n")
//                .filter {it.isNotBlank()}
//                .map { it.trim() }
//
//                for(i in paragraphs.indices) {
//                    builder.putBlock(BlockType.PARAGRAPH, i == paragraphs.lastIndex) {
//                        val splittedText = CommentTagsExtractor.extract(paragraphs[i])
//
//                        for(j in splittedText.indices) {
//                            val type = if(splittedText[j].isTag) BlockType.TAG else BlockType.TEXT
//
//                            builder.putBlock(type, j == splittedText.lastIndex, splittedText[j].text)
//                        }
//                    }
//                }
//        }
//
//        return builder.build()
        return ""
    }

    /**
     * Image only
     */
    fun mapImage(imageUri: String):String {
        return ""

//        val builder = JsonBuilderImpl.create()
//
//        builder.putBlock(
//            BlockType.POST,
//            true,
//            PostAttribute(
//                Attribute.VERSION,
//                PostGlobalConstants.postFormatVersion.toString()
//            ),
//            PostAttribute(
//                Attribute.TYPE,
//                PostTypeJson.COMMENT
//            )
//        ) {
//
//            builder.putBlock(BlockType.IMAGE, true, imageUri)
//        }
//
//        return builder.build()
    }
}