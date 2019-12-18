package io.golos.data.mappers

import io.golos.data.dto.block.ContentAttribute
import io.golos.data.dto.block.ContentBlockEntity
import io.golos.data.dto.block.ContentEntity
import io.golos.data.dto.block.ListContentBlockEntity
import io.golos.domain.use_cases.post.TextStyle
import io.golos.domain.use_cases.post.post_dto.*

fun ContentBlock.mapToContentBlock(): ListContentBlockEntity {
    val contentBlock = mutableListOf<ContentBlockEntity>()

    if (content.isNotEmpty()) {
        contentBlock.add(ContentBlockEntity("paragraph", content.mapToBlockEntity()))
    }

    attachments?.content?.let { content ->
        contentBlock.add(ContentBlockEntity("attributes", content.mapToBlockEntity()))
    }

    return ListContentBlockEntity(contentBlock)
}

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
        ContentAttribute(
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
        content = content,
        attributes = attribute,
        type = "text"
    )
}

fun TagBlock.mapToEntity(): ContentEntity {
    return ContentEntity(content = content, type = "tag")
}

fun MentionBlock.mapToEntity(): ContentEntity {
    return ContentEntity(content = content, type = "mention")
}

fun LinkBlock.mapToEntity(): ContentEntity {
    return ContentEntity(
        content = content,
        type = "link",
        attributes = ContentAttribute(url = url.toString())
    )
}

fun WebsiteBlock.mapToEntity(): ContentEntity {
    return ContentEntity(
        content = content.toString(),
        type = "website",
        attributes = ContentAttribute(
            thumbnailUrl = thumbnailUrl?.toString(),
            title = title,
            providerName = providerName,
            description = description
        )
    )
}

fun EmbedBlock.mapToEntity(): ContentEntity {
    return ContentEntity(
        content = content.toString(),
        type = "embed",
        attributes = ContentAttribute(
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
        content = content.toString(),
        type = "image",
        attributes = ContentAttribute(description = description)
    )
}

fun VideoBlock.mapToEntity(): ContentEntity {
    return ContentEntity(
        content = content.toString(),
        type = "video",
        attributes = ContentAttribute(
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
        content = content.toString(),
        type = "rich",
        attributes = ContentAttribute(
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