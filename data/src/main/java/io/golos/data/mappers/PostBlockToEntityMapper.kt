package io.golos.data.mappers

import io.golos.domain.dto.block.*
import io.golos.domain.use_cases.post.TextStyle
import io.golos.domain.use_cases.post.post_dto.*

fun ContentBlock.mapToContentBlock(): ListContentBlockEntity {
    val contentBlock = mutableListOf<ContentBlockEntity>()

    if (content.isNotEmpty()) {
        val contentBlocks = content.map { ContentBlockEntity((it as? ParagraphBlock)?.id, "paragraph", content.mapToBlockEntity()) }
        contentBlock.addAll(contentBlocks)
    }

    attachments?.content?.let { content ->
        contentBlock.add(ContentBlockEntity(attachments?.id,"attachments", content.mapToBlockEntity()))
    }

    return ListContentBlockEntity(
        this.id,
        this.type,
        this.metadata.mapToDocumentAttributes(),
        contentBlock)
}

fun PostMetadata.mapToDocumentAttributes(): DocumentAttributeEntity = DocumentAttributeEntity(this.version.toString(), this.type.name.toLowerCase())

fun List<Block>.mapToBlockEntity(): List<ContentEntity> {
    return map { block ->
        when (block) {
            is TextBlock -> block.mapToEntity()
            is TagBlock -> block.mapToEntity()
            is MentionBlock -> block.mapToEntity()
            is EmbedBlock -> block.mapToEntity()
            is ImageBlock -> block.mapToEntity()
            is LinkBlock -> block.mapToEntity()
            is RichBlock -> block.mapToEntity()
            is VideoBlock -> block.mapToEntity()
            is WebsiteBlock -> block.mapToEntity()
            is ParagraphBlock -> {
                when (val paragraphBlock = block.content.first()) {
                    is LinkBlock -> paragraphBlock.mapToEntity()
                    is MentionBlock -> paragraphBlock.mapToEntity()
                    is TagBlock -> paragraphBlock.mapToEntity()
                    is TextBlock -> paragraphBlock.mapToEntity()
                    else -> throw RuntimeException("Error with ContentBlock mapper")
                }
            }
            else -> throw RuntimeException("Error with ContentBlock mapper")
        }
    }
}


fun TextBlock.mapToEntity(): ContentEntity {
    val attribute = if (style != null || textColor != null) {
        ContentAttributeEntity(
            style = style?.let { style ->
                when (style) {
                    TextStyle.BOLD -> listOf("bold")
                    TextStyle.ITALIC -> listOf("italic")
                    TextStyle.BOLD_ITALIC -> listOf("bold", "italic")
                }
            },
            textColor = textColor?.let { color ->
                String.format("#%06X", 0xFFFFFF and color)
            }
        )
    } else null
    return ContentEntity(
        id = id,
        content = content,
        attributes = attribute,
        type = "text"
    )
}

fun TagBlock.mapToEntity(): ContentEntity {
    return ContentEntity(id = id, content = content, type = "tag")
}

fun MentionBlock.mapToEntity(): ContentEntity {
    return ContentEntity(id = id, content = content, type = "mention")
}

fun LinkBlock.mapToEntity(): ContentEntity {
    return ContentEntity(
        id = id,
        content = content,
        type = "link",
        attributes = ContentAttributeEntity(url = url.toString())
    )
}

fun WebsiteBlock.mapToEntity(): ContentEntity {
    return ContentEntity(
        id = id,
        content = content.toString(),
        type = "website",
        attributes = ContentAttributeEntity(
            thumbnailUrl = thumbnailUrl?.toString(),
            title = title,
            providerName = providerName,
            description = description
        )
    )
}

fun EmbedBlock.mapToEntity(): ContentEntity {
    return ContentEntity(
        id = id,
        content = content.toString(),
        type = "embed",
        attributes = ContentAttributeEntity(
            title = title,
            url = url?.toString(),
            author = author,
            authorUrl = authorUrl?.toString(),
            providerName = providerName,
            description = description,
            thumbnailUrl = thumbnailUrl?.toString(),
            thumbnailWidth = thumbnailWidth,
            thumbnailHeight = thumbnailHeight,
            html = html
        )
    )
}

fun ImageBlock.mapToEntity(): ContentEntity {
    return ContentEntity(
        id = id,
        content = content.toString(),
        type = "image",
        attributes = description?.let { ContentAttributeEntity(description = description) }
    )
}

fun VideoBlock.mapToEntity(): ContentEntity {
    return ContentEntity(
        id = id,
        content = content.toString(),
        type = "video",
        attributes = ContentAttributeEntity(
            title = title,
            url = content.toString(),
            author = author,
            authorUrl = authorUrl?.toString(),
            providerName = providerName,
            description = description,
            thumbnailUrl = thumbnailUrl?.toString(),
            thumbnailWidth = thumbnailSize?.width,
            thumbnailHeight = thumbnailSize?.height,
            html = html
        )
    )
}

fun RichBlock.mapToEntity(): ContentEntity {
    return ContentEntity(
        id = id,
        content = content.toString(),
        type = "rich",
        attributes = ContentAttributeEntity(
            title = title,
            url = url?.toString(),
            author = author,
            authorUrl = authorUrl?.toString(),
            providerName = providerName,
            description = description,
            thumbnailUrl = thumbnailUrl?.toString(),
            thumbnailWidth = thumbnailWidth,
            thumbnailHeight = thumbnailHeight,
            html = html
        )
    )
}