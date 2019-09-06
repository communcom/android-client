package io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers_factory

import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.BlockType
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.html_builder.HtmlBuilder
import io.golos.cyber_android.ui.shared_fragments.post.view_holders.post_text.renderering.renderers.*

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