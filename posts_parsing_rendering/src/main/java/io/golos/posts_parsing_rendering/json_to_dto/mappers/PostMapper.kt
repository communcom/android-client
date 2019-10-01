package io.golos.posts_parsing_rendering.json_to_dto.mappers

import io.golos.domain.post.post_dto.*
import io.golos.posts_parsing_rendering.Attribute
import io.golos.posts_parsing_rendering.BlockType
import io.golos.posts_parsing_rendering.GlobalConstants
import io.golos.posts_parsing_rendering.PostTypeJson
import io.golos.posts_parsing_rendering.json_to_dto.IncompatibleVersionsException
import org.json.JSONObject

class PostMapper(mappersFactory: MappersFactory): MapperBase<PostBlock>(mappersFactory) {
    override fun map(source: JSONObject): PostBlock {
        val jsonAttributes = source.getAttributes() ?: throw IllegalArgumentException("Post attributes can't be empty")

        val version = PostFormatVersion.parse(jsonAttributes.getString(Attribute.VERSION))

        if(version.major > GlobalConstants.postFormatVersion.major) {
            throw IncompatibleVersionsException()
        }

        val title = jsonAttributes.tryString(Attribute.TITLE)

        val postType = jsonAttributes.getString(Attribute.TYPE).toPostType()

        val jsonContent = source.getContentAsArray()

        val content = mutableListOf<Block>()
        var attachments: AttachmentsBlock? = null

        for(i in 0 until jsonContent.length()) {
            jsonContent.getJSONObject(i)
                .also { block ->
                    when(val type = block.getType()) {
                        BlockType.PARAGRAPH -> {
                            content.add(mappersFactory.getMapper<ParagraphMapper>(ParagraphMapper::class).map(block))
                        }

                        BlockType.IMAGE -> {
                            content.add(mappersFactory.getMapper<ImageMapper>(ImageMapper::class).map(block))
                        }

                        BlockType.VIDEO -> {
                            content.add(mappersFactory.getMapper<VideoMapper>(VideoMapper::class).map(block))
                        }

                        BlockType.WEBSITE -> {
                            content.add(mappersFactory.getMapper<WebsiteMapper>(WebsiteMapper::class).map(block))
                        }

                        BlockType.ATTACHMENTS -> {
                            attachments = mappersFactory.getMapper<AttachmentsMapper>(AttachmentsMapper::class).map(block)
                        }
                        else -> throw UnsupportedOperationException("This type ob block is not supported here: $type")
                    }
                }

        }

        return PostBlock(version, title, postType, content, attachments)
    }

    private fun String.toPostType(): PostType =
        when(this) {
            PostTypeJson.ARTICLE -> PostType.ARTICLE
            PostTypeJson.BASIC -> PostType.BASIC
            PostTypeJson.COMMENT -> PostType.COMMENT
            else -> throw java.lang.UnsupportedOperationException("This type of post is not supported: $this")
        }
}