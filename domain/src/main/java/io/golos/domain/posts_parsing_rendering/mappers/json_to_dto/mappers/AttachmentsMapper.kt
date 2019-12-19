package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.posts_parsing_rendering.BlockType
import io.golos.domain.posts_parsing_rendering.CommonType
import io.golos.domain.use_cases.post.post_dto.AttachmentsBlock
import io.golos.domain.use_cases.post.post_dto.MediaBlock
import org.json.JSONObject

class AttachmentsMapper(mappersFactory: MappersFactory) : MapperBase<AttachmentsBlock>(mappersFactory) {
    override fun map(source: JSONObject): AttachmentsBlock {
        val jsonContent = source.getContentAsArray()

        val content = mutableListOf<MediaBlock>()

        for (i in 0 until jsonContent.length()) {
            jsonContent.getJSONObject(i)
                .also { json ->
                    val block = when (json.getType()) {
                        BlockType.IMAGE -> {
                            mappersFactory.getMapper<ImageMapper>(ImageMapper::class).map(json)
                        }
                        BlockType.VIDEO -> {
                            mappersFactory.getMapper<VideoMapper>(VideoMapper::class).map(json)
                        }
                        BlockType.WEBSITE -> {
                            mappersFactory.getMapper<WebsiteMapper>(WebsiteMapper::class).map(json)
                        }
                        BlockType.RICH -> {
                            mappersFactory.getMapper<RichMapper>(RichMapper::class).map(json)
                        }
                        BlockType.EMBED -> {
                            mappersFactory.getMapper<EmbedMapper>(EmbedMapper::class).map(json)
                        }
                        else -> null
                    }
                    if (block != null) {
                        content.add(block)
                    }
                }
        }

        return AttachmentsBlock(source.tryString(CommonType.ID),
            content)
    }
}