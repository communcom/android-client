package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers_factory

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.BlockType
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.links_repository.LinksRepository
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers.*

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

            BlockType.LINK -> LinkRenderer(builder, linksRepository)
            BlockType.IMAGE -> ImageRenderer(builder, linksRepository)
            BlockType.VIDEO -> VideoRenderer(builder, linksRepository)
            BlockType.WEBSITE -> WebsiteRenderer(builder, linksRepository)

            else -> throw UnsupportedOperationException("This type of block is not supported: $type")
        }

}