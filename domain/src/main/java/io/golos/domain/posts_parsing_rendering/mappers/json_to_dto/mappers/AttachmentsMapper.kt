package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.use_cases.post.post_dto.AttachmentsBlock
import io.golos.domain.use_cases.post.post_dto.MediaBlock
import io.golos.domain.posts_parsing_rendering.BlockType
import org.json.JSONObject

class AttachmentsMapper(mappersFactory: MappersFactory): MapperBase<AttachmentsBlock>(mappersFactory) {
    override fun map(source: JSONObject): AttachmentsBlock {
        val jsonContent = source.getContentAsArray()

        val content = mutableListOf<MediaBlock>()

        for(i in 0 until jsonContent.length()) {
            jsonContent.getJSONObject(i)
                .also {
                    val block = when(val type = it.getType()) {
                        BlockType.IMAGE -> mappersFactory.getMapper<ImageMapper>(
                            ImageMapper::class).map(it)
                        BlockType.VIDEO -> mappersFactory.getMapper<VideoMapper>(
                            VideoMapper::class).map(it)
                        BlockType.WEBSITE -> mappersFactory.getMapper<WebsiteMapper>(
                            WebsiteMapper::class).map(it)
                        else -> throw UnsupportedOperationException("This type ob block is not supported here: $type")
                    }

                    content.add(block)
                }
        }

        return AttachmentsBlock(content)
    }
}