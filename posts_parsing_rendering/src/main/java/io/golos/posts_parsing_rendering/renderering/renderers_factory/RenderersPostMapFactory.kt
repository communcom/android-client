package io.golos.posts_parsing_rendering.renderering.renderers_factory

import io.golos.posts_parsing_rendering.renderering.BlockType
import io.golos.posts_parsing_rendering.renderering.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.renderering.renderers.*

@Suppress("SpellCheckingInspection")
class RenderersPostMapFactory(builder: HtmlBuilder) : RenderersFactoryBase(builder) {
    override fun createRenderer(type: String): RendererBase =
        when(type) {
            BlockType.LINK -> LinkPostMapRenderer(builder)
            BlockType.IMAGE -> ImagePostMapRenderer(builder)
            BlockType.VIDEO -> VideoPostMapRenderer(builder)
            BlockType.WEBSITE -> WebsitePostMapRenderer(builder)

            else -> throw UnsupportedOperationException("This type of block is not supported: $type")
        }

}