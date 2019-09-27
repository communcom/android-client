package io.golos.posts_parsing_rendering.metadata_to_json

import io.golos.domain.post_editor.*
import io.golos.posts_parsing_rendering.Attribute
import io.golos.posts_parsing_rendering.BlockType
import io.golos.posts_parsing_rendering.GlobalConstants
import io.golos.posts_parsing_rendering.LinkTypeJson
import io.golos.posts_parsing_rendering.metadata_to_json.builder.JsonBuilderBlocks
import io.golos.posts_parsing_rendering.metadata_to_json.builder.JsonBuilderImpl
import io.golos.posts_parsing_rendering.metadata_to_json.builder.PostAttribute
import io.golos.posts_parsing_rendering.metadata_to_json.spans_splitter.*

class MetadataToJsonMapper() {
    @Suppress("NestedLambdaShadowedImplicitParameter")
    fun map(postMetadata: List<ControlMetadata>): String {
        val builder = JsonBuilderImpl.create()
        val spansSplitter = SpansSplitter()

        builder.putBlock(BlockType.POST, true, PostAttribute(Attribute.VERSION, GlobalConstants.postFormatVersion)) {
            val paragraphs = postMetadata.filterIsInstance<ParagraphMetadata>()
            val embeds = postMetadata.filterIsInstance<EmbedMetadata>()

            paragraphs.forEachIndexed { index, paragraph ->
                val isLast = index == paragraphs.lastIndex && embeds.isEmpty()
                putParagraph(paragraph, it, isLast, spansSplitter)
            }

            if(embeds.isNotEmpty()) {
                it.putBlock(BlockType.ATTACHMENTS, true) {
                    putEmbed(embeds.first(), it)
                }
            }
        }

        return builder.build()
    }

    private fun putEmbed(metadataBlock: EmbedMetadata, builder: JsonBuilderBlocks) {
        builder.putBlock(metadataBlock.type.mapToBlockType(), true, metadataBlock.sourceUri.toString())
    }

    private fun EmbedType.mapToBlockType(): BlockType =
        when(this) {
            EmbedType.LOCAL_IMAGE,
            EmbedType.EXTERNAL_IMAGE -> BlockType.IMAGE
            EmbedType.EXTERNAL_VIDEO -> BlockType.VIDEO
            EmbedType.EXTERNAL_WEBSITE -> BlockType.WEBSITE
        }

    private fun putParagraph(metadataBlock: ParagraphMetadata, builder: JsonBuilderBlocks, isLast: Boolean, splitter: SpansSplitter) {
        if(metadataBlock.plainText.isEmpty()) {
            return
        }

        builder.putBlock(BlockType.PARAGRAPH, isLast) {
            val textItems = splitter.split(metadataBlock)

            textItems.forEachIndexed { index, split ->
                val isLastSplit = index == textItems.indices.last

                when(split) {
                    is SplittedText -> builder.putBlock(BlockType.TEXT, isLastSplit, split.content)

                    is SplittedTag -> builder.putBlock(BlockType.TAG, isLastSplit, split.content)

                    is SplittedMention -> builder.putBlock(BlockType.MENTION, isLastSplit, split.content)

                    is SplittedLink -> builder.putBlock(
                        BlockType.LINK,
                        isLastSplit,
                        split.content,
                        PostAttribute(Attribute.URL, split.uri.toString()),
                        PostAttribute(Attribute.TYPE, split.type.map()),
                        PostAttribute(Attribute.THUMBNAIL_URL, split.thumbnailUrl.toString())
                    )
                }
            }
        }
    }

    private fun LinkType.map(): String =
        when(this) {
            LinkType.IMAGE -> LinkTypeJson.IMAGE
            LinkType.VIDEO -> LinkTypeJson.VIDEO
            LinkType.WEBSITE -> LinkTypeJson.WEBSITE
        }
}