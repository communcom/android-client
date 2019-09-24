package io.golos.posts_parsing_rendering.renderering.renderers_factory

import io.golos.posts_parsing_rendering.renderering.BlockType
import io.golos.posts_parsing_rendering.renderering.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.renderering.links_repository.LinksRepository
import io.golos.posts_parsing_rendering.renderering.renderers.*

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
            BlockType.POST -> PostRenderer(this, builder)
            BlockType.PARAGRAPH -> ParagraphRenderer(this, builder)

            BlockType.TEXT -> TextRenderer(builder)
            BlockType.TAG -> TagRenderer(builder)
            BlockType.MENTION -> MentionRenderer(builder)

            BlockType.LINK -> LinkRenderer(builder, linksRepository)
            BlockType.IMAGE -> ImageRenderer(builder, linksRepository)
            BlockType.VIDEO -> VideoRenderer(builder, linksRepository)
            BlockType.WEBSITE -> WebsiteRenderer(builder, linksRepository)

            else -> throw UnsupportedOperationException("This type of block is not supported: $type")
        }

}