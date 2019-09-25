package io.golos.posts_parsing_rendering.json_to_html.renderers_factory

import io.golos.posts_parsing_rendering.BlockType
import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.json_to_html.links_repository.LinksRepository
import io.golos.posts_parsing_rendering.json_to_html.renderers.*

/**
 * Creates renderers for a message text
 */
@Suppress("SpellCheckingInspection")
class RenderersMessageFactory(
    builder: HtmlBuilder,
    private val linksRepository: LinksRepository
) : RenderersFactoryBase(builder) {
    override fun createRenderer(type: String): RendererBase =
        when(type) {
            BlockType.POST.value -> PostRenderer(this, builder)
            BlockType.PARAGRAPH.value -> ParagraphRenderer(this, builder)

            BlockType.TEXT.value -> TextRenderer(builder)
            BlockType.TAG.value -> TagRenderer(builder)
            BlockType.MENTION.value -> MentionRenderer(builder)

            BlockType.LINK.value -> LinkRenderer(builder, linksRepository)
            BlockType.IMAGE.value -> ImageRenderer(builder, linksRepository)
            BlockType.VIDEO.value -> VideoRenderer(builder, linksRepository)
            BlockType.WEBSITE.value -> WebsiteRenderer(builder, linksRepository)

            else -> throw UnsupportedOperationException("This type of block is not supported: $type")
        }
}