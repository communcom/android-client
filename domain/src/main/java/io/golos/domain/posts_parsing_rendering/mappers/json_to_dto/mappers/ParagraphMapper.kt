package io.golos.domain.posts_parsing_rendering.mappers.json_to_dto.mappers

import io.golos.domain.use_cases.post.post_dto.ParagraphBlock
import io.golos.domain.use_cases.post.post_dto.ParagraphItemBlock
import io.golos.domain.posts_parsing_rendering.BlockType
import org.json.JSONObject

class ParagraphMapper(mappersFactory: MappersFactory): MapperBase<ParagraphBlock>(mappersFactory) {
    override fun map(source: JSONObject): ParagraphBlock {
        val jsonContent = source.getContentAsArray()

        val content = mutableListOf<ParagraphItemBlock>()

        for(i in 0 until jsonContent.length()) {
            jsonContent.getJSONObject(i)
                .also { block ->
                    val textBlock = when(val type = block.getType()) {
                        BlockType.TEXT -> mappersFactory.getMapper<TextMapper>(
                            TextMapper::class).map(block)
                        BlockType.TAG -> mappersFactory.getMapper<TagMapper>(
                            TagMapper::class).map(block)
                        BlockType.MENTION -> mappersFactory.getMapper<MentionMapper>(
                            MentionMapper::class).map(block)
                        BlockType.LINK -> mappersFactory.getMapper<LinkMapper>(
                            LinkMapper::class).map(block)
                        else -> throw UnsupportedOperationException("This type ob block is not supported here: $type")
                    }

                    content.add(textBlock)
                }
        }

        return ParagraphBlock(content)
    }
}