package io.golos.posts_parsing_rendering.json_to_html.renderers_factory

import io.golos.posts_parsing_rendering.BlockType
import io.golos.posts_parsing_rendering.json_to_html.html_builder.HtmlBuilder
import io.golos.posts_parsing_rendering.json_to_html.renderers.*

@Suppress("SpellCheckingInspection")
class RenderersPostMapFactory(builder: HtmlBuilder) : RenderersFactoryBase(builder) {
    override fun createRenderer(type: String): RendererBase =
        when(type) {
            BlockType.LINK.value -> LinkPostMapRenderer(builder)
            BlockType.IMAGE.value -> ImagePostMapRenderer(builder)
            BlockType.VIDEO.value -> VideoPostMapRenderer(builder)
            BlockType.WEBSITE.value -> WebsitePostMapRenderer(builder)

            else -> throw UnsupportedOperationException("This type of block is not supported: $type")
        }
}